/*
 * Copyright 2018 Nikolaus Knop
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package memento.dummys

import memento.DoNotMemorize
import memento.SelfMemorable
import java.util.*

private enum class E { A, B, C }

class TestMemorable private constructor(
    private val a: Boolean = false,
    private val b: Byte = 0,
    private val c: Short = 0,
    private val char: Char = ' ',
    private val d: Int = 0,
    private val e: Long = 0,
    private val f: Float = 0.0F,
    private val g: Double = 0.0,
    private val string: String = "",
    private val i: Array<Int> = arrayOf(),
    private val h: BooleanArray = booleanArrayOf(),
    private val j: ByteArray = byteArrayOf(),
    private val k: ShortArray = shortArrayOf(),
    private val l: IntArray = intArrayOf(),
    private val m: LongArray = longArrayOf(),
    private val n: FloatArray = floatArrayOf(),
    private val o: DoubleArray = doubleArrayOf(),
    private val p: Date = Date(0),
    private val q: Country = Country(),
    private val r: Byte? = null,
    private val s: Country? = null,
    private val u: Date? = null,
    private val v: Int = 0,
    private val w: List<Int> = emptyList(),
    private val x: Array<String> = emptyArray(),
    @DoNotMemorize private val dnm: Int = 0,
    private val enum: E = E.A
): SelfMemorable {

    companion object {
        fun createAdapter() = TestMemorable()

        fun construct(): TestMemorable {
            return TestMemorable(
                true,
                12,
                239,
                'z',
                3499,
                34923904,
                23.0F,
                34.0,
                "hello world",
                arrayOf(1, 2, 3),
                booleanArrayOf(true, false),
                byteArrayOf(0, 34, 2),
                shortArrayOf(349, 304, 23),
                intArrayOf(34, 234, 2304),
                longArrayOf(2349, 23940, 23049),
                floatArrayOf(4809324.0F, 3489.23F),
                doubleArrayOf(349.0, 349.234),
                Date(23023),
                Country("Madagaskar"),
                null,
                null,
                null,
                394,
                listOf(434, 34934, 2340),
                arrayOf("hey", "du"),
                23,
                E.B
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TestMemorable

        if (a != other.a) return false
        if (b != other.b) return false
        if (c != other.c) return false
        if (char != other.char) return false
        if (d != other.d) return false
        if (e != other.e) return false
        if (f != other.f) return false
        if (g != other.g) return false
        if (string != other.string) return false
        if (!Arrays.equals(i, other.i)) return false
        if (!Arrays.equals(h, other.h)) return false
        if (!Arrays.equals(j, other.j)) return false
        if (!Arrays.equals(k, other.k)) return false
        if (!Arrays.equals(l, other.l)) return false
        if (!Arrays.equals(m, other.m)) return false
        if (!Arrays.equals(n, other.n)) return false
        if (!Arrays.equals(o, other.o)) return false
        if (p != other.p) return false
        if (q != other.q) return false
        if (r != other.r) return false
        if (s != other.s) return false
        if (u != other.u) return false
        if (v != other.v) return false
        if (w != other.w) return false
        if (!Arrays.equals(x, other.x)) return false
        if (enum != other.enum) return false
        if (dnm == other.dnm) return false //Yeah really
        return true
    }

    override fun hashCode(): Int {
        var result = a.hashCode()
        result = 31 * result + b
        result = 31 * result + c
        result = 31 * result + d
        result = 31 * result + e.hashCode()
        result = 31 * result + f.hashCode()
        result = 31 * result + g.hashCode()
        result = 31 * result + string.hashCode()
        result = 31 * result + Arrays.hashCode(i)
        result = 31 * result + Arrays.hashCode(h)
        result = 31 * result + Arrays.hashCode(j)
        result = 31 * result + Arrays.hashCode(k)
        result = 31 * result + Arrays.hashCode(l)
        result = 31 * result + Arrays.hashCode(m)
        result = 31 * result + Arrays.hashCode(n)
        result = 31 * result + Arrays.hashCode(o)
        result = 31 * result + p.hashCode()
        result = 31 * result + q.hashCode()
        result = 31 * result + (r ?: 0)
        result = 31 * result + (s?.hashCode() ?: 0)
        result = 31 * result + (u?.hashCode() ?: 0)
        result = 31 * result + v
        result = 31 * result + w.hashCode()
        result = 31 * result + x.hashCode()
        result = 31 * result + dnm
        return result
    }
}