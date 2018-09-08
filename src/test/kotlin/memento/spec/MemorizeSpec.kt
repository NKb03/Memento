/*
 * Copyright 2018 Nikolaus Knop
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 *@author Nikolaus Knop
 */

package memento.spec

import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.should.shouldMatch
import com.natpryce.hamkrest.throws
import memento.*
import memento.dummys.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.util.*

internal object MemorizeSpec: Spek({
    given("a memorizer") {
        val memorizer = Memorizer.newInstance()
        memorizer.adapterRegistrar.registerAdapter<Country, CountryAdapter>()
        memorizer.adapterRegistrar.registerAdapter<Date, DateMementoAdapter>()
        val testMemorable = TestMemorable.construct()
        lateinit var memory: Memento
        describe("memorizing") {
            on("memorizing a person") {
                memory = memorizer.memorize(testMemorable)
                it("should throw no exception") {}
            }
            on("remembering it") {
                val newTest = memorizer.remember(memory) as TestMemorable
                it("should return the original person") {
                    newTest shouldMatch equalTo(testMemorable)
                }
            }
            on("memorizing an object that is not Memorable and has no adapter specified") {
                val error = { memorizer.memorize(Unmemorable()); Unit }
                it("should throw a MementoAdapterConfigurationException") {
                    error shouldMatch throws<MementoAdapterConfigurationException>()
                }
            }
            on("memorizing a memorable that returns an invalid memento adapter") {
                val error = { memorizer.memorize(MementoAdapterTypeMismatch()); Unit }
                it("should throw a MementoAdapterConfigurationException") {
                    error shouldMatch throws<MementoAdapterConfigurationException>()
                }
            }
            on("memorizing an anonymous object") {
                val anonymous = object : Memorable {
                    override val mementoAdapter: MementoAdapter<*> get() = error("")
                }
                val error = { memorizer.memorize(anonymous); Unit }
                it("should throw a MemorizingException") {
                    error shouldMatch throws<MemorizingException>()
                }
            }
        }
        describe("remembering") {
            on("remembering a memorable that does not have a companion object") {
                val mem = memorizer.memorize(NoCompanionObject())
                val error = { memorizer.remember(mem); Unit }
                it("should throw a MementoAdapterConfigurationException") {
                    error shouldMatch throws<MementoAdapterConfigurationException>()
                }
            }
            on("remembering a memorable that does not have a function createAdapter in companion object") {
                val mem = memorizer.memorize(NoDefaultAdapterConstructor())
                val error = { memorizer.remember(mem); Unit }
                it("should throw a MementoAdapterConfigurationException") {
                    error shouldMatch throws<MementoAdapterConfigurationException>()
                }
            }
            on("remembering a memorable whose fun createAdapter in companion object has the wrong signature") {
                val mem = memorizer.memorize(WrongSignatureDefaultAdapterConstructor())
                val error = { memorizer.remember(mem); Unit }
                it("should throw a MementoAdapterConfigurationException") {
                    error shouldMatch throws<MementoAdapterConfigurationException>()
                }
            }
            on("remembering a memento to an instance of another class") {
                val mem = memorizer.memorize(Country("hello"))
                val otherInstance = 23
                val error = { memorizer.remember(otherInstance, mem) }
                it("should throw an illegal argument exception") {
                    error shouldMatch throws<IllegalArgumentException>()
                }
            }
        }
    }
})

