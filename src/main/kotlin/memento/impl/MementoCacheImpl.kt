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

import memento.Memento
import memento.MementoCache
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.HashMap

class MementoCacheImpl(private val capacity: Int, private val removeStrategy: MementoCache.RemoveStrategy)
    : MementoCache {
    init {
        require(capacity > 0) { "capacity was $capacity but has to be greater than 0" }
        GarbageCollectionListener.onGarbageCollection {
            var r = refQueue.poll()
            while (r != null) {
                val obj = r.get()
                val memento = map[r]!!
                removed(obj, memento)
                r = refQueue.poll()
            }
        }
    }

    private val refQueue = ReferenceQueue<Any>()

    private val map = HashMap<WeakReference<Any>, Memento>(capacity)

    private val removeListeners = LinkedList<(Any?, Memento) -> Unit>()

    override fun push(obj: Any, memento: Memento) {
        if (capacity == map.size) {
            remove()
        }
        val weakRef = WeakReference(obj, refQueue)
        map[weakRef] = memento
    }

    private fun remove() {
        val removedObject = removeStrategy.objectToRemove(map.keys)
        val removedMemento = map[removedObject]!!
        removed(removedMemento, removedMemento)
    }

    private fun removed(obj: Any?, memento: Memento) {
        for (listener in removeListeners) {
            listener.invoke(obj, memento)
        }
    }

    override fun poll(obj: Any): Memento? {
        return map.remove(obj)
    }

    override fun addRemoveListener(listener: (Any?, Memento) -> Unit) {
        removeListeners.add(listener)
    }

    override fun deleteRemoveListener(listener: (Any?, Memento) -> Unit) {
        removeListeners.remove(listener)
    }
}
