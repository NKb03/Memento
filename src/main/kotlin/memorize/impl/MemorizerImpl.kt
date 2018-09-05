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

package memorize.impl

import memorize.*
import kotlin.reflect.KClass
import kotlin.system.measureTimeMillis

internal class MemorizerImpl : Memorizer {
    override val adapterRegistrar = MementoAdapterRegistrarImpl()

    @Suppress("UNCHECKED_CAST")
    override fun memorize(obj: Any): Memento {
        val cls = obj::class
        if (cls.qualifiedName == null) {
            val msg = "Instances of anonymous classes or anonymous objects cannot be memorized"
            val ex = MemorizingException(msg)
            throw ex
        }
        val adapter = adapterRegistrar.createAdapter(obj)
        val memorizedProperties = MemorizedProperties.of(adapter::class)
        val values = memorizedProperties.map { p ->
            val v = p.get(adapter)
            Value.of(v, this) as Value<Any?>
        }
        @Suppress("UNCHECKED_CAST") val casted = cls as KClass<Any>
        return MementoImpl(casted, values)
    }

    override fun remember(memento: Memento): Any {
        require(memento is MementoImpl) { "Invalid type of $memento" }
        memento as MementoImpl
        val cls = memento.cls
        val adapter = adapterRegistrar.createAdapter(cls)
        rememberTo(adapter, memento)
        return adapter.memorized
    }

    override fun remember(instance: Any, memento: Memento) {
        require(memento is MementoImpl) { "Invalid type of $memento" }
        memento as MementoImpl
        if (!memento.cls.isInstance(instance)) {
            val msg = "Instance is of type ${instance::class} but memento has class ${memento.cls}"
            val ex = IllegalArgumentException(msg)
            throw ex
        }
        val adapter = adapterRegistrar.createAdapter(instance)
        rememberTo(adapter, memento)
    }

    private fun rememberTo(adapter: MementoAdapter<Any>, memento: MementoImpl) {
        val properties = MemorizedProperties.of(adapter::class)
        val values = memento.properties
        for ((i, p) in properties.withIndex()) {
            if (i !in values.indices) {
                val msg = "No value for $p specified in $memento"
                val ex = RememberException(msg)
                throw ex
            }
            val value = values[i].value
            if (value is Memento) {
                val remembered = remember(value)
                p.set(adapter, remembered)
            } else {
                p.set(adapter, value)
            }
        }
    }
}