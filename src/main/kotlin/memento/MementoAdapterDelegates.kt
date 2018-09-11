/**
 * @author Nikolaus Knop
 */

package memento

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

private class FieldDelegate<T>(
    private val p: KProperty<T>,
    private val receiver: Any?
) : ReadWriteProperty<Any?, T> {
    private val field = p.javaField ?: throw IllegalArgumentException("$p has no backing field")

    init {
        field.isAccessible = true
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return p.call(thisRef)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        field.set(receiver, value)
    }
}

fun <T> field(property: KProperty<T>, receiver: Any): ReadWriteProperty<Any?, T>
        = FieldDelegate(property, receiver)
