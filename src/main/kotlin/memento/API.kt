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

import memento.impl.Reflection
import kotlin.reflect.KClass

/**
 * Syntactic sugar for registerAdapter converting the type parameters to [KClass]s
*/
inline fun <reified T: Any, reified A: MementoAdapter<T>> MementoAdapterRegistrar.registerAdapter() {
    @Suppress("UNCHECKED_CAST") registerAdapter(T::class, A::class as KClass<MementoAdapter<T>>)
}

/**
 * Registers a constructor that tries to create an instance of [adapterCls]
 * with the no-arg and then with a one-arg constructor
*/
fun <T : Any> MementoAdapterRegistrar.registerAdapter(cls: KClass<in T>, adapterCls: KClass<out MementoAdapter<T>>) {
    registerAdapter(
        cls,
        { instance -> Reflection.createAdapter(adapterCls, instance) },
        { Reflection.createAdapter(adapterCls) }
    )
}