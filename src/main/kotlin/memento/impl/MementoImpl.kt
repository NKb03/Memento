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

import memento.Memento
import java.io.OutputStream
import kotlin.reflect.KClass

/**
 * A Memento represents an object that stores only the information of an object to be able to reconstruct it
*/
internal class MementoImpl(
    val cls: KClass<Any>,
    val properties: List<Value<Any?>>
) : Memento {
    override fun toString(): String {
        return buildString {
            appendln("Memory: $cls {")
            for ((i, v) in properties.withIndex()) {
                appendln("  $i: $v")
            }
            append("{")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MementoImpl

        if (cls != other.cls) return false
        if (properties != other.properties) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cls.hashCode()
        result = 31 * result + properties.hashCode()
        return result
    }

    override fun writeTo(output: OutputStream) {
        Serializer.writeMemento(this, output)
    }
}