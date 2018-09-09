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

import memento.Memorizer
import java.util.*
import kotlin.reflect.KClass

internal sealed class Value<T> {
    abstract val value: T
    abstract val serializer: Serializer<T>

    data class BooleanValue(override val value: Boolean) : Value<Boolean>() {
        override val serializer: Serializer<Boolean>
            get() = if (value) Serializer.TRUE else Serializer.FALSE
    }

    data class ByteValue(override val value: Byte) : Value<Byte>() {
        override val serializer
            get() = Serializer.BYTE
    }

    data class ShortValue(override val value: Short) : Value<Short>() {
        override val serializer
            get() = Serializer.SHORT
    }

    data class CharValue(override val value: Char) : Value<Char>() {
        override val serializer
            get() = Serializer.CHAR
    }

    data class IntValue(override val value: Int) : Value<Int>() {
        override val serializer
            get() = Serializer.INT
    }

    data class LongValue(override val value: Long) : Value<Long>() {
        override val serializer
            get() = Serializer.LONG
    }

    data class FloatValue(override val value: Float) : Value<Float>() {
        override val serializer
            get() = Serializer.FLOAT
    }

    data class DoubleValue(override val value: Double) : Value<Double>() {
        override val serializer
            get() = Serializer.DOUBLE
    }

    data class StringValue(override val value: String) : Value<String>() {
        override val serializer
            get() = Serializer.STRING
    }

    data class ArrayValue(override val value: Pair<Array<Value<Any?>>, Class<*>>)
        : Value<Pair<Array<Value<Any?>>, Class<*>>>() {
        override val serializer
            get() = Serializer.ARRAY

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ArrayValue
            val (arr, _) = value
            if (!Arrays.equals(arr, other.value.first)) return false

            return true
        }

        override fun hashCode(): Int {
            return Arrays.hashCode(value.first)
        }
    }

    data class BooleanArrayValue(override val value: BooleanArray) : Value<BooleanArray>() {
        override val serializer
            get() = Serializer.BOOLEAN_ARRAY

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as BooleanArrayValue

            if (!Arrays.equals(value, other.value)) return false

            return true
        }

        override fun hashCode(): Int {
            return Arrays.hashCode(value)
        }
    }

    data class ByteArrayValue(override val value: ByteArray) : Value<ByteArray>() {
        override val serializer
            get() = Serializer.BYTE_ARRAY

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ByteArrayValue

            if (!Arrays.equals(value, other.value)) return false

            return true
        }

        override fun hashCode(): Int {
            return Arrays.hashCode(value)
        }
    }

    data class ShortArrayValue(override val value: ShortArray) : Value<ShortArray>() {
        override val serializer
            get() = Serializer.SHORT_ARRAY

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ShortArrayValue

            if (!Arrays.equals(value, other.value)) return false

            return true
        }

        override fun hashCode(): Int {
            return Arrays.hashCode(value)
        }
    }

    data class CharArrayValue(override val value: CharArray) : Value<CharArray>() {
        override val serializer
            get() = Serializer.CHAR_ARRAY

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CharArrayValue

            if (!Arrays.equals(value, other.value)) return false

            return true
        }

        override fun hashCode(): Int {
            return Arrays.hashCode(value)
        }
    }

    data class IntArrayValue(override val value: IntArray) : Value<IntArray>() {
        override val serializer
            get() = Serializer.INT_ARRAY

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as IntArrayValue

            if (!Arrays.equals(value, other.value)) return false

            return true
        }

        override fun hashCode(): Int {
            return Arrays.hashCode(value)
        }
    }

    data class LongArrayValue(override val value: LongArray) : Value<LongArray>() {
        override val serializer
            get() = Serializer.LONG_ARRAY

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as LongArrayValue

            if (!Arrays.equals(value, other.value)) return false

            return true
        }

        override fun hashCode(): Int {
            return Arrays.hashCode(value)
        }
    }

    data class FloatArrayValue(override val value: FloatArray) : Value<FloatArray>() {
        override val serializer
            get() = Serializer.FLOAT_ARRAY

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as FloatArrayValue

            if (!Arrays.equals(value, other.value)) return false

            return true
        }

        override fun hashCode(): Int {
            return Arrays.hashCode(value)
        }
    }

    data class DoubleArrayValue(override val value: DoubleArray) : Value<DoubleArray>() {
        override val serializer
            get() = Serializer.DOUBLE_ARRAY

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as DoubleArrayValue

            if (!Arrays.equals(value, other.value)) return false

            return true
        }

        override fun hashCode(): Int {
            return Arrays.hashCode(value)
        }
    }

    data class EnumValue(override val value: Enum<*>) : Value<Enum<*>>() {
        override val serializer
            get() = Serializer.ENUM
    }

    data class MementoValue(override val value: MementoImpl) : Value<MementoImpl>() {
        override val serializer
            get() = Serializer.MEMENTO
    }

    object Null : Value<Nothing?>() {
        override val serializer
            get() = Serializer.NULL
        override val value: Nothing?
            get() = null
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        private infix fun <T : Any> KClass<T>.toConstructor(constructor: (T) -> Value<T>): Pair<KClass<*>, (Any) -> Value<*>> {
            return this to { v: Any -> constructor(v as T) }
        }

        @Suppress("UNCHECKED_CAST")
        internal fun of(value: Any?, memorizer: Memorizer): Value<*> {
            return when (value) {
                null -> Null
                is Boolean -> BooleanValue(value)
                is Byte -> ByteValue(value)
                is Short -> ShortValue(value)
                is Char -> CharValue(value)
                is Int -> IntValue(value)
                is Long -> LongValue(value)
                is Float -> FloatValue(value)
                is Double -> DoubleValue(value)
                is String -> StringValue(value)
                is BooleanArray -> BooleanArrayValue(value)
                is ByteArray -> ByteArrayValue(value)
                is ShortArray -> ShortArrayValue(value)
                is IntArray -> IntArrayValue(value)
                is LongArray -> LongArrayValue(value)
                is FloatArray -> FloatArrayValue(value)
                is DoubleArray -> DoubleArrayValue(value)
                is Array<*> -> {
                    val values = value.mapToArray { Value.of(it, memorizer) }
                    val componentType = value::class.java.componentType
                    ArrayValue(values as Array<Value<Any?>> to componentType)
                }
                is Enum<*> -> EnumValue(value)
                else -> MementoValue(memorizer.memorize(value) as MementoImpl)
            }
        }

    }
}
