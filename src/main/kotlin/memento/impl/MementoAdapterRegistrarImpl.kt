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

package memento.impl

import memento.*
import memento.impl.adapters.DateMementoAdapter
import memento.impl.adapters.ListMementoAdapter
import memento.impl.adapters.MapMementoAdapter
import memento.impl.adapters.SetMementoAdapter
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.*

@Suppress("UNCHECKED_CAST")
internal class MementoAdapterRegistrarImpl : MementoAdapterRegistrar {
    private val userSpecified: MutableMap<KClass<in Any>, Pair<(Any) -> MementoAdapter<Any>, () -> MementoAdapter<Any>>>
            = mutableMapOf()

    init {
        registerDefaultAdapters()
    }

    private fun registerDefaultAdapters() {
        registerAdapter<List<*>, ListMementoAdapter>()
        registerAdapter<Set<*>, SetMementoAdapter>()
        registerAdapter<Map<*, *>, MapMementoAdapter>()
        registerAdapter<Date, DateMementoAdapter>()
    }


    override fun <T : Any> registerAdapter(
        cls: KClass<in T>, constructor: (T) -> MementoAdapter<T>, defaultConstructor: () -> MementoAdapter<T>
    ) {
        val castedConstructor = constructor as (Any) -> MementoAdapter<Any>
        val pair = castedConstructor to defaultConstructor
        userSpecified[cls as KClass<in Any>] = pair
    }

    fun <T : Any> createAdapter(instance: T): MementoAdapter<T> {
        val cls = instance::class
        val customAdapterConstructor = getCustomAdapterConstructor(cls)
        if (customAdapterConstructor != null) {
            return customAdapterConstructor(instance)
        }
        return createDefaultAdapter(instance, cls)
    }

    fun <T : Any> createAdapter(cls: KClass<out T>): MementoAdapter<T> {
        cls as KClass<T>
        val customAdapterConstructor = customDefaultAdapterConstructor(cls)
        if (customAdapterConstructor != null) {
            return customAdapterConstructor()
        }
        return createDefaultAdapter(cls)
    }

    private fun <T : Any> createDefaultAdapter(cls: KClass<T>): MementoAdapter<T> {
        if (!cls.isSubclassOf(Memorable::class)) {
            val msg = "No custom memento adapter specified for $cls"
            val ex = MementoAdapterConfigurationException(msg)
            throw ex
        }
        val companion = cls.companionObject
        if (companion == null) {
            val msg = "Memorable $cls has no companion object"
            val ex = MementoAdapterConfigurationException(msg)
            throw ex
        }
        val createAdapterFun = companion.functions.find { f ->
            val nameFits = f.name == CREATE_ADAPTER
            val classifier = f.returnType.classifier
            val returnTypeFits = classifier is KClass<*> && classifier.isSubclassOf(MementoAdapter::class)
            val parametersEmpty = f.valueParameters.isEmpty()
            nameFits && returnTypeFits && parametersEmpty
        }
        val companionInstance = companion.objectInstance!!
        if (createAdapterFun == null) {
            val msg =
                    """Could not find function $CREATE_ADAPTER with no parameters
                        |and return type of MementoAdapter in companion object of $cls""".trimMargin()
            val ex = MementoAdapterConfigurationException(msg)
            throw ex
        }
        return createAdapterFun.call(companionInstance) as MementoAdapter<T>
    }

    private fun <T : Any> customDefaultAdapterConstructor(cls: KClass<out T>): (() -> MementoAdapter<T>)? {
        val adapterConstructor = userSpecified[cls as KClass<in Any>]?.second
        if (adapterConstructor != null) return adapterConstructor as (() -> MementoAdapter<T>)?
        val superClasses = cls.superclasses
        for (sc in superClasses) {
            val superClassAdapterConstructor = customDefaultAdapterConstructor(sc)
            if (superClassAdapterConstructor != null) return superClassAdapterConstructor as (() -> MementoAdapter<T>)
        }
        return null
    }


    private fun <T : Any> createDefaultAdapter(instance: T, cls: KClass<out T>): MementoAdapter<T> {
        if (instance !is Memorable) {
            val msg = "Could not create an adapter class for $instance of $cls"
            val ex = MementoAdapterConfigurationException(msg)
            throw ex
        }
        val adapter = instance.mementoAdapter
        if (!cls.isInstance(adapter.memorized)) {
            val msg = "Invalid memento adapter $adapter for $cls"
            val ex = MementoAdapterConfigurationException(msg)
            throw ex
        }
        return adapter as MementoAdapter<T>
    }

    private fun <T : Any> getCustomAdapterConstructor(cls: KClass<out T>): ((T) -> MementoAdapter<T>)? {
        val adapterConstructor = userSpecified[cls as KClass<in Any>]?.first
        if (adapterConstructor != null) return adapterConstructor as ((T) -> MementoAdapter<T>)?
        val superClasses = cls.superclasses
        for (sc in superClasses) {
            val superClassAdapterConstructor = getCustomAdapterConstructor(sc)
            if (superClassAdapterConstructor != null) return superClassAdapterConstructor as ((T) -> MementoAdapter<T>)?
        }
        return null
    }

    companion object {
        private const val CREATE_ADAPTER = "createAdapter"
    }
}

