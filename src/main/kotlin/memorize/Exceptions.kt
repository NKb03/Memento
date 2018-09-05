/*
 * Copyright 2018 Nikolaus Knop
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package memorize

/**
 * Root class for all exceptions that are thrown by the memorize API
*/
abstract class MemorizeException internal constructor(message: String? = null, cause: Throwable? = null)
    : Exception(message, cause)

/**
 * Occurs while serializing a [Memento] to an output stream
*/
class MementoSerializationException internal constructor(message: String? = null, cause: Throwable? = null)
    : MemorizeException(message, cause)

/**
 * Occurs while deserializing a [Memento] from an input stream
*/
open class MementoDeserializationException internal constructor(message: String? = null, cause: Throwable? = null)
    : MemorizeException(message, cause)

private const val WRONG_DATA_FORMAT_MESSAGE =
        "Cannot read a memento from this input stream because it is in the wrong format"

/**
 * Occurs when a memento is tried to read from invalid input
*/
class WrongDataFormatException internal constructor(message: String? = WRONG_DATA_FORMAT_MESSAGE, cause: Throwable? = null)
    : MementoDeserializationException(message, cause)

/**
 * Occurs while reconstructing an object from a [Memento]
*/
class RememberException internal constructor(message: String? = null, cause: Throwable? = null)
    : MemorizeException(message, cause)

/**
 * Occurs when the memento adapter configuration is not valid
*/
class MementoAdapterConfigurationException internal constructor(message: String? = null, cause: Throwable? = null)
    : MemorizeException(message, cause)

/**
 * Thrown when an error occurs during memorizing an object
*/
class MemorizingException internal constructor(message: String? = null, cause: Throwable? = null)
    : MemorizeException(message, cause)