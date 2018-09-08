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

import com.sun.management.GarbageCollectionNotificationInfo
import java.lang.management.ManagementFactory
import java.util.*
import javax.management.NotificationEmitter
import javax.management.NotificationListener

object GarbageCollectionListener {
    private val gcListeners = LinkedList<() -> Unit>()

    init {
        listenForGC()
    }

    fun onGarbageCollection(callback: () -> Unit): () -> Unit {
        gcListeners.add(callback)
        return { gcListeners.remove(callback) }
    }

    private fun listenForGC() {
        val beans = ManagementFactory.getGarbageCollectorMXBeans()
        for (b in beans) {
            b as NotificationEmitter
            val l = NotificationListener { notification, _ ->
                if (notification.type == GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION) {
                    garbageCollectionHappened()
                }
            }
            b.addNotificationListener(l, null, null)
        }
    }

    private fun garbageCollectionHappened() {
        gcListeners.forEach { listener -> listener.invoke() }
    }
}