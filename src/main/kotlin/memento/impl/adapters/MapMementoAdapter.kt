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

package memento.impl.adapters

import memento.MementoAdapter

internal class MapMementoAdapter(override val memorized: Map<Any?, Any?> = mutableMapOf())
    : MementoAdapter<Map<Any?, Any?>> {
    var entries: Array<Pair<Any?, Any?>>
        get() {
            val arr = Array<Pair<Any?, Any?>>(memorized.size) { null to null }
            for ((i, entry) in memorized.entries.withIndex()) {
                val (k, v) = entry
                arr[i] = k to v
            }
            return arr
        }
        set(pairs) {
            memorized as MutableMap
            memorized.putAll(pairs)
        }
}