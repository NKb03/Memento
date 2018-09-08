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
 * @author Nikolaus Knop
 */

package memento.impl

import memento.MementoDeserializationException
import memento.MementoSerializationException
import memento.WrongDataFormatException
import memento.impl.Value.*
import java.io.*
import kotlin.reflect.KClass
import kotlin.reflect.full.staticFunctions
import kotlin.reflect.jvm.isAccessible

@Suppress("ClassName")
internal sealed class Serializer<T>(private val byteFlag: Byte) {
    fun writeValue(value: T, out: DataOutput) {
        out.writeByte(byteFlag)
        doWriteValue(value, out)
    }

    protected abstract fun doWriteValue(value: T, out: DataOutput)
    abstract fun readValue(input: DataInput): Value<T>

    fun register(): Pair<Byte, Serializer<*>> = byteFlag to this

    object NULL : Serializer<Nothing?>(-1) {
        override fun readValue(input: DataInput) = Null

        override fun doWriteValue(value: Nothing?, out: DataOutput) {}
    }

    object FALSE : Serializer<Boolean>(0) {
        override fun readValue(input: DataInput) = BooleanValue(false)

        override fun doWriteValue(value: Boolean, out: DataOutput) {}
    }

    object TRUE : Serializer<Boolean>(1) {
        override fun doWriteValue(value: Boolean, out: DataOutput) {}
        override fun readValue(input: DataInput) = BooleanValue(true)
    }

    object BYTE : Serializer<Byte>(2) {
        override fun doWriteValue(value: Byte, out: DataOutput) {
            out.writeByte(value)
        }

        override fun readValue(input: DataInput): ByteValue {
            val b = input.readByte()
            return ByteValue(b)
        }
    }

    object SHORT : Serializer<Short>(3) {
        override fun doWriteValue(value: Short, out: DataOutput) {
            out.writeShort(value.toInt())
        }

        override fun readValue(input: DataInput): ShortValue {
            val s = input.readShort()
            return ShortValue(s)
        }
    }

    object CHAR: Serializer<Char>(4) {
        override fun doWriteValue(value: Char, out: DataOutput) {
            out.writeChar(value.toInt())
        }

        override fun readValue(input: DataInput): Value<Char> {
            val c = input.readChar()
            return CharValue(c)
        }
    }

    object INT : Serializer<Int>(5) {
        override fun doWriteValue(value: Int, out: DataOutput) {
            out.writeInt(value)
        }

        override fun readValue(input: DataInput): IntValue {
            val i = input.readInt()
            return IntValue(i)
        }
    }

    object LONG : Serializer<Long>(6) {
        override fun doWriteValue(value: Long, out: DataOutput) {
            out.writeLong(value)
        }

        override fun readValue(input: DataInput): LongValue {
            val l = input.readLong()
            return LongValue(l)
        }
    }

    object FLOAT : Serializer<Float>(7) {
        override fun readValue(input: DataInput): FloatValue {
            val f = input.readFloat()
            return FloatValue(f)
        }

        override fun doWriteValue(value: Float, out: DataOutput) {
            out.writeFloat(value)
        }
    }

    object DOUBLE : Serializer<Double>(8) {
        override fun doWriteValue(value: Double, out: DataOutput) {
            out.writeDouble(value)
        }

        override fun readValue(input: DataInput): DoubleValue {
            val d = input.readDouble()
            return DoubleValue(d)
        }
    }

    object STRING : Serializer<String>(9) {
        override fun doWriteValue(value: String, out: DataOutput) {
            out.writeUTF(value)
        }

        override fun readValue(input: DataInput): StringValue {
            val str = input.readUTF()
            return StringValue(str)
        }
    }

    object ARRAY : Serializer<Array<Any?>>(10) {
        override fun doWriteValue(value: Array<Any?>, out: DataOutput) {
            val componentCls = value::class.java.componentType
            out.writeClassName(componentCls)
            out.writeShort(value.size)
            for (e in value) {
                write(out, e)
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun readValue(input: DataInput): ArrayValue {
            val componentClsName = input.readUTF()
            val componentCls = Class.forName(componentClsName)
            val size = input.readShort().toInt()
            val arr = java.lang.reflect.Array.newInstance(componentCls, size) as Array<Any?>
            repeat(size) { i ->
                arr[i] = read(input).value
            }
            return ArrayValue(arr)
        }
    }

    object BOOLEAN_ARRAY : Serializer<BooleanArray>(11) {
        override fun doWriteValue(value: BooleanArray, out: DataOutput) {
            out.writeShort(value.size)
            for (b in value) {
                out.writeBoolean(b)
            }
        }

        override fun readValue(input: DataInput): BooleanArrayValue {
            val size = input.readShort().toInt()
            val arr = BooleanArray(size) { input.readBoolean() }
            return BooleanArrayValue(arr)
        }
    }

    object BYTE_ARRAY : Serializer<ByteArray>(12) {
        override fun doWriteValue(value: ByteArray, out: DataOutput) {
            out.writeShort(value.size)
            for (b in value) {
                out.writeByte(b)
            }
        }

        override fun readValue(input: DataInput): ByteArrayValue {
            val size = input.readShort().toInt()
            val arr = ByteArray(size) { input.readByte() }
            return ByteArrayValue(arr)
        }
    }

    object SHORT_ARRAY : Serializer<ShortArray>(13) {
        override fun doWriteValue(value: ShortArray, out: DataOutput) {
            out.writeShort(value.size)
            for (s in value) {
                out.writeShort(s)
            }
        }

        override fun readValue(input: DataInput): ShortArrayValue {
            val size = input.readShort().toInt()
            val arr = ShortArray(size) { input.readShort() }
            return ShortArrayValue(arr)
        }
    }

    object CHAR_ARRAY : Serializer<CharArray>(13) {
        override fun doWriteValue(value: CharArray, out: DataOutput) {
            out.writeShort(value.size)
            for (c in value) {
                out.writeChar(c.toInt())
            }
        }

        override fun readValue(input: DataInput): CharArrayValue {
            val size = input.readChar().toInt()
            val arr = CharArray(size) { input.readChar() }
            return CharArrayValue(arr)
        }
    }


    object INT_ARRAY : Serializer<IntArray>(15) {
        override fun doWriteValue(value: IntArray, out: DataOutput) {
            out.writeShort(value.size)
            for (i in value) {
                out.writeInt(i)
            }
        }

        override fun readValue(input: DataInput): IntArrayValue {
            val size = input.readShort().toInt()
            val arr = IntArray(size) { input.readInt() }
            return IntArrayValue(arr)
        }
    }

    object LONG_ARRAY : Serializer<LongArray>(16) {
        override fun doWriteValue(value: LongArray, out: DataOutput) {
            out.writeShort(value.size)
            for (l in value) {
                out.writeLong(l)
            }
        }

        override fun readValue(input: DataInput): LongArrayValue {
            val size = input.readShort().toInt()
            val arr = LongArray(size) { input.readLong() }
            return LongArrayValue(arr)
        }
    }

    object FLOAT_ARRAY : Serializer<FloatArray>(17) {
        override fun doWriteValue(value: FloatArray, out: DataOutput) {
            out.writeShort(value.size)
            for (f in value) {
                out.writeFloat(f)
            }
        }

        override fun readValue(input: DataInput): FloatArrayValue {
            val size = input.readShort().toInt()
            val arr = FloatArray(size) { input.readFloat() }
            return FloatArrayValue(arr)
        }
    }

    object DOUBLE_ARRAY : Serializer<DoubleArray>(18) {
        override fun doWriteValue(value: DoubleArray, out: DataOutput) {
            out.writeShort(value.size)
            for (d in value) {
                out.writeDouble(d)
            }
        }

        override fun readValue(input: DataInput): DoubleArrayValue {
            val size = input.readShort().toInt()
            val arr = DoubleArray(size) { input.readDouble() }
            return DoubleArrayValue(arr)
        }
    }


    object ENUM : Serializer<Enum<*>>(19) {
        override fun doWriteValue(value: Enum<*>, out: DataOutput) {
            val cls = value::class
            cls.qualifiedName!!
            out.writeClassName(cls)
            out.writeInt(value.ordinal)
        }

        @Suppress("UNCHECKED_CAST")
        override fun readValue(input: DataInput): Value<Enum<*>> {
            val clsName = input.readUTF()
            val cls = Class.forName(clsName).kotlin
            val ordinal = input.readInt()
            val valuesFun = cls.staticFunctions.find { it.name == "values" && it.parameters.isEmpty() }!!
            valuesFun.isAccessible = true
            val values = valuesFun.call() as Array<Enum<*>>
            val value = values[ordinal]
            return EnumValue(value)
        }
    }
    object MEMENTO : Serializer<MementoImpl>(MEMENTO_BEGIN_FLAG) {

        override fun doWriteValue(value: MementoImpl, out: DataOutput) {
            val className = value.cls.qualifiedName!!
            val converted = Reflection.convertNestedClassName(className)
            out.writeUTF(converted)
            val propertiesSize = value.properties.size
            out.writeShort(propertiesSize)
            for (p in value.properties) {
                val serializer = p.serializer
                serializer.writeValue(p.value, out)
            }
        }
        @Suppress("UNCHECKED_CAST")
        override fun readValue(input: DataInput): MementoValue {
            val clsName = input.readUTF()
            val propertiesSize = input.readShort().toInt()
            val properties = List(propertiesSize) { read(input) }
            val cls = Class.forName(clsName).kotlin as KClass<Any>
            val memento = MementoImpl(cls, properties)
            return MementoValue(memento)
        }

    }
    companion object {

        private fun DataOutput.writeByte(b: Byte) {
            writeByte(b.toInt())
        }

        private fun DataOutput.writeShort(s: Short) {
            writeShort(s.toInt())
        }

        private const val MEMENTO_BEGIN_FLAG: Byte = 20

        private val byteFlagsToValueTypes =
                mapOf(
                    NULL.register(),
                    FALSE.register(),
                    TRUE.register(),
                    BYTE.register(),
                    SHORT.register(),
                    CHAR.register(),
                    INT.register(),
                    LONG.register(),
                    FLOAT.register(),
                    DOUBLE.register(),
                    STRING.register(),
                    ARRAY.register(),
                    BOOLEAN_ARRAY.register(),
                    BYTE_ARRAY.register(),
                    SHORT_ARRAY.register(),
                    INT_ARRAY.register(),
                    LONG_ARRAY.register(),
                    FLOAT_ARRAY.register(),
                    DOUBLE_ARRAY.register(),
                    ENUM.register(),
                    MEMENTO.register()
                )

        @Suppress("UNCHECKED_CAST")
        private fun getSerializer(byteFlag: Byte): Serializer<Any?> {
            val serializer: Serializer<*> =
                    byteFlagsToValueTypes[byteFlag]
                    ?: throw NoSuchElementException("Unknown byte flag $byteFlag")
            return serializer as Serializer<Any?>
        }

        private val classToValueTypes =
                mapOf(
                    Byte::class to BYTE,
                    Short::class to SHORT,
                    Char::class to CHAR,
                    Int::class to INT,
                    Long::class to LONG,
                    Float::class to FLOAT,
                    Double::class to DOUBLE,
                    String::class to STRING,
                    Array<Any?>::class to ARRAY,
                    BooleanArray::class to BOOLEAN_ARRAY,
                    ByteArray::class to BYTE_ARRAY,
                    ShortArray::class to SHORT_ARRAY,
                    IntArray::class to INT_ARRAY,
                    LongArray::class to LONG_ARRAY,
                    FloatArray::class to FLOAT_ARRAY,
                    DoubleArray::class to DOUBLE_ARRAY,
                    Enum::class to ENUM,
                    MementoImpl::class to MEMENTO
                )

        @Suppress("UNCHECKED_CAST")
        private fun get(value: Any?): Serializer<Any?> {
            return when (value) {
                null -> NULL as Serializer<Any?>
                false -> FALSE as Serializer<Any?>
                true -> TRUE as Serializer<Any?>
                else -> {
                    val cls = value::class
                    return classToValueTypes[cls] as Serializer<Any?>
                }
            }
        }

        private fun read(input: DataInput): Value<Any?> {
            val byteFlag = input.readByte()
            val serializer = getSerializer(byteFlag)
            return serializer.readValue(input)
        }

        private fun write(out: DataOutput, value: Any?) {
            val serializer = Serializer.get(value)
            serializer.writeValue(value, out)
        }

        fun readMemento(input: InputStream): MementoImpl {
            try {
                val buffered = input.buffered()
                val dataInput = DataInputStream(buffered)
                val mementoBeginFlag = dataInput.readByte()
                if (mementoBeginFlag != MEMENTO_BEGIN_FLAG) {
                    throw WrongDataFormatException()
                }
                val memento = MEMENTO.readValue(dataInput).value
                dataInput.close()
                buffered.close()
                input.close()
                return memento

            } catch (unknownByteFlag: NoSuchElementException) {
                throw WrongDataFormatException(cause = unknownByteFlag)
            } catch (eof: EOFException) {
                throw WrongDataFormatException(cause = eof)
            } catch (ex: IOException) {
                throw MementoDeserializationException("IO exception while reading memento", cause = ex)
            }
        }
        fun writeMemento(memento: MementoImpl, output: OutputStream) {
            try {
                val buffered = output.buffered()
                val dataOutput = DataOutputStream(buffered)
                MEMENTO.writeValue(memento, dataOutput)
                dataOutput.close()
                buffered.close()
                output.close()
            } catch (ex: IOException) {
                throw MementoSerializationException("IO exception while writing memento", cause = ex)
            }
        }

        private fun DataOutput.writeClassName(clazz: Class<*>) {
            val componentClsName = Reflection.convertNestedClassName(clazz.name)
            writeUTF(componentClsName)
        }

        private fun DataOutput.writeClassName(kClass: KClass<*>) {
            writeClassName(kClass.java)
        }
    }
}

