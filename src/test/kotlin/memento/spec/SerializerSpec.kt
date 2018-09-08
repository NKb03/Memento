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
import memento.dummys.Country
import memento.dummys.CountryAdapter
import memento.dummys.DateMementoAdapter
import memento.dummys.TestMemorable
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.io.*
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ThreadLocalRandom

internal object SerializerSpec: Spek({
    lateinit var mem: Memento
    given("a memory of a test memorable") {
        val test = TestMemorable.construct()
        val memorizer = Memorizer.newInstance()
        memorizer.adapterRegistrar.registerAdapter<Country, CountryAdapter>()
        memorizer.adapterRegistrar.registerAdapter<Date, DateMementoAdapter>()
        mem = memorizer.memorize(test)
        val baos = ByteArrayOutputStream()
        on("serializing the memory") {
            mem.writeTo(baos)
            it("should throw no exception") {}
        }
        on("deserializing the memory") {
            val bais = ByteArrayInputStream(baos.toByteArray())
            val newMemento = Memento.readFrom(bais)
            it("should return the original memento") {
                newMemento shouldMatch equalTo(mem)
            }
            memorizer.remember(newMemento)
            it("should recreate the memory so that it represents the original object") {
                memorizer.remember(newMemento) as TestMemorable shouldMatch equalTo(test)
            }
        }
    }
    given("a byte array input stream which doesn't match the memento format") {
        val byteArray = ByteArray(1000)
        ThreadLocalRandom.current().nextBytes(byteArray)
        val bais = ByteArrayInputStream(byteArray)
        on("trying to read a memento from this input stream") {
            val error = { Memento.readFrom(bais); Unit }
            it("should throw a WrongDataFormatException") {
                error shouldMatch throws<WrongDataFormatException>()
            }
        }
    }
    given("a possibly valid file input stream") {
        val file = Paths.get("test.mem").toFile()
        file.createNewFile()
        val fis = FileInputStream(file)
        on("closing it before reading a memento") {
            fis.close()
            it("should throw a MementoDeserializationException") {
                { Memento.readFrom(fis); Unit } shouldMatch throws<MementoDeserializationException>()
            }
        }
    }
    given("a file output stream") {
        val path = Paths.get("test.mem")
        val fos = FileOutputStream(path.toFile())
        on("closing it before writing a memento") {
            fos.close()
            it("should throw MementoSerializationException") {
                { mem.writeTo(fos); Unit } shouldMatch throws<MementoSerializationException>()
            }
        }
        path.toFile().delete()
    }
})