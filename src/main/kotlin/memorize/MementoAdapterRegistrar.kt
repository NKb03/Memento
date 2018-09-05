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

package memorize

import kotlin.reflect.KClass

/**
 * Used to specify [MementoAdapter]s that are not specified by the [Memorable] annotation
 * * There are already memento adapters registered for
 * * [kotlin.collections.List]
 * * [kotlin.collections.Map]
 * * [kotlin.collections.Set]
 *
*/
interface MementoAdapterRegistrar {
    /**
     * Registers [constructor] and [defaultConstructor] to [cls]
     * * That means that when instances of [cls] are memorized [constructor] is invoked with the memorized instances as
     * argument and all properties of the adapter are written to the [Memento]
     * * When instances of [cls] are remembered at first the adapter is created with [defaultConstructor] and then all
     * properties of the [Memento] are written to the adapter which delegates to the actual instance
     * * When there are already constructors and default constructors for the adapters for [cls] registered or if
     * [cls] is a subclass of [Memorable] then this call overrides the constructors
    */
    fun <T : Any> registerAdapter(
        cls: KClass<in T>,
        constructor: (T) -> MementoAdapter<T>,
        defaultConstructor: () -> MementoAdapter<T>
    )
}