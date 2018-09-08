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

package memento.bigobjects

import memento.MementoAdapter
import memento.Memorable

internal sealed class Tree: Memorable, MementoAdapter<Tree> {
    override val mementoAdapter
        get() = this

    override val memorized: Tree
        get() = this

    object Leaf: Tree()
    data class Branch(private var left: Tree = Leaf, private var right: Tree = Leaf): Tree()

    companion object {
        fun big(deep: Int): Tree {
            return when (deep) {
                0 -> Leaf
                else -> {
                    val left = big(deep - 1)
                    val right = big(deep - 1)
                    Branch(left, right)
                }
            }
        }
    }
}