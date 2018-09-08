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

package memento

import memento.impl.MemorizerImpl

/**
 * A [Memorizer] is used to convert objects to [Memento]s and the other way around
 */
interface Memorizer {
    /**
     * The [MementoAdapterRegistrar] that is used to obtain [MementoAdapter]s for objects
    */
    val adapterRegistrar: MementoAdapterRegistrar

    /**
     * Creates a [Memento] representing [obj]
     * @throws MemorizingException if [obj] is an anonymous object
     * @throws MementoAdapterConfigurationException if the memorizer can't construct a [MementoAdapter]
    */
    fun memorize(obj: Any): Memento

    /**
     * Writes the [memento] into [instance]
     * @throws IllegalArgumentException if the class of [instance] doesn't match the class of the [memento]
     * @throws RememberException if the properties in [memento] don't match those of [instance]
     * @throws MementoAdapterConfigurationException if the memorizer can't construct a [MementoAdapter]
    */
    fun remember(instance: Any, memento: Memento)

    /**
     * Reconstructs an object from [memento]
     * @throws RememberException if the properties in the class of the memento don't match those of [memento]
     * @throws MementoAdapterConfigurationException if the memorizer can't construct a [MementoAdapter]
    */
    fun remember(memento: Memento): Any

    companion object {
        /**
         * Creates a new [Memorizer]
         */
        fun newInstance(): Memorizer = MemorizerImpl()
    }
}