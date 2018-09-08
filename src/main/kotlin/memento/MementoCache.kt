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

package memento

import memento.impl.MementoCacheImpl

/**
 * * A cache which associates objects with their mementos
 * * A cache has a capacity
 *      * when a new memento is pushed to a cache and the capacity is exceeded a memento is removed from the cache
 *      * you can specify strategy to remove mementos
 *  * Since the objects are stored using [java.lang.ref.WeakReference] pushing them into a [MementoCache]
 *  doesn't prevent them from being garbage collected
 */
interface MementoCache {
    /**
     * Associates [obj] with [memento] such that it can be later regain it with [poll]
    */
    fun push(obj: Any, memento: Memento)

    /**
     * Returns the [Memento] associated with [obj] and removes it from the cache
    */
    fun poll(obj: Any): Memento?

    /**
     * Adds a [listener] that is called when a [Memento] associated with and object is
     * removed from this cache either because the capacity was exceeded or because an object was garbage collected
     * In the latter case the first argument of [listener] will be `null`
    */
    fun addRemoveListener(listener: (Any?, Memento) -> Unit)

    /**
     * Removes a [listener] that was previously added with [addRemoveListener]
    */
    fun deleteRemoveListener(listener: (Any?, Memento) -> Unit)


    /**
     * Used to remove objects from the cache if the capacity is exceeded
    */
    interface RemoveStrategy {
        /**
         * @return an object out of [objects] that should be removed
        */
        fun objectToRemove(objects: Set<Any>): Any

        /**
         * Default [RemoveStrategy] which returns a random element
        */
        object Random: RemoveStrategy {
            override fun objectToRemove(objects: Set<Any>): Any = objects.first()
        }
    }

    companion object {
        /**
         * Creates a new [MementoCache] with [capacity] and [removeStrategy]
        */
        fun newInstance(capacity: Int, removeStrategy: RemoveStrategy = RemoveStrategy.Random): MementoCache {
            return MementoCacheImpl(capacity, removeStrategy)
        }
    }
}
