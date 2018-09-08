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

import memento.MementoAdapter
import memento.MementoAdapterConfigurationException
import java.util.*
import kotlin.reflect.*
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.valueParameters

internal object Reflection {
    fun <T : Any> createAdapter(adapterCls: KClass<out MementoAdapter<T>>)
            : MementoAdapter<T> {
        try {
            return adapterCls.createInstance()
        }
        catch (ex: IllegalArgumentException) {
            val msg = "Could not create an instance of $adapterCls"
            throw MementoAdapterConfigurationException(msg, ex)
        }
    }

    fun <T: Any> createAdapter(adapterCls: KClass<out MementoAdapter<T>>, instance: T): MementoAdapter<T> {
        @Suppress("UNCHECKED_CAST") if (adapterCls == instance::class) {
            return instance as MementoAdapter<T>
        }
        val type = instance::class.starProjectedType
        val constructor = adapterCls.oneArgConstructor(type)
        if (constructor == null) {
            val msg = "Could not create an instance of $adapterCls for $instance because no constructor fits"
            throw MementoAdapterConfigurationException(msg)
        }
        return constructor.call(instance)
    }

    private fun <T : Any> KClass<T>.oneArgConstructor(paramType: KType): KFunction<T>? {
        return constructors.find { c ->
            val params = c.valueParameters
            params.isNotEmpty() &&
            params.first().type.isSupertypeOf(paramType)
            && params.drop(1).all { p -> p.isOptional }
        }
    }

    fun convertNestedClassName(className: String): String {
        val sc = Scanner(className).useDelimiter("\\.")
        return buildString(className.length) {
            for (n in sc) {
                append(n)
                if (n.first().isLowerCase()) append(".")
                else append("$")
            }
            deleteCharAt(className.length)
        }
    }
}