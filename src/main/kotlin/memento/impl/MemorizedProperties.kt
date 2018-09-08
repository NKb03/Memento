/*
 * Copyright 2018 Nikolaus Knop
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package memento.impl

import memento.*
import java.lang.reflect.Field
import kotlin.reflect.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

internal object MemorizedProperties {
    private val cache =
            mutableMapOf<KClass<out MementoAdapter<*>>, List<MemorizedProperty>>()

    @Suppress("UNCHECKED_CAST")
    private fun KClass<*>.memorizedProperties(): List<MemorizedProperty> {
        val properties = memberProperties
        return properties.mapNotNull { p ->
            val field = p.javaField
            when {
                p.name == "memorized" -> null
                p.name == "mementoAdapter" -> null
                p.hasAnnotation<DoNotMemorize>() -> null
                field != null -> FieldMemorizedProperty(p, field)
                p is KMutableProperty1 -> KPropertyMemorizedProperty(p as KMutableProperty1<Any, Any?>)
                else -> null
            }
        }
    }

    private inline fun <reified A: Annotation> KAnnotatedElement.hasAnnotation(): Boolean =
            findAnnotation<A>() != null

    @Suppress("UNCHECKED_CAST")
    fun of(cls: KClass<out MementoAdapter<*>>): List<MemorizedProperty> {
        return cache.getOrPut(cls) { cls.memorizedProperties() }
    }
}

internal interface MemorizedProperty {
    fun set(receiver: Any, value: Any?)

    fun get(receiver: Any): Any?
}

private class FieldMemorizedProperty(val property: KProperty1<*, *>, private val field: Field): MemorizedProperty {
    override fun set(receiver: Any, value: Any?) {
        field.isAccessible = true
        field.set(receiver, value)
    }

    override fun get(receiver: Any): Any? {
        field.isAccessible = true
        return field.get(receiver)
    }

    override fun toString(): String {
        return property.toString()
    }
}

private class KPropertyMemorizedProperty(val property: KMutableProperty1<Any, Any?>): MemorizedProperty {
    override fun set(receiver: Any, value: Any?) {
        property.isAccessible = true
        property.set(receiver, value)
    }

    override fun get(receiver: Any): Any? {
        property.isAccessible = true
        return property.get(receiver)
    }

    override fun toString(): String {
        return property.toString()
    }
}

