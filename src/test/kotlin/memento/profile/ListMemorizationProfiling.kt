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

package memento.profile

import memento.Memorizer
import memento.dummys.Country
import memento.dummys.CountryAdapter
import memento.dummys.TestMemorable
import nikok.kprofile.api.profile
import memento.registerAdapter
import nikok.kprofile.api.markdown
import nikok.kprofile.api.view

fun profileListMemorization() = profile("list memorization", currentTags) {
    withMemorizer(Memorizer.newInstance().apply {
        adapterRegistrar.registerAdapter<Country, CountryAdapter>()
    }) {
        memorizing("a small list of ints") {
            List(10) { it }
        }
        memorizing("a average sized list of ints") {
            List(100) { it }
        }
        memorizing("a big list of ints") {
            List(10_000) { it }
        }
        memorizing("a small list of average sized objects") {
            List(10) { Country("HELLO WORLD") }
        }
        memorizing("a average sized list of average sized objects") {
            List(1000) { Country("HELLO WORLD") }
        }
        memorizing("a big list of average sized objects") {
            List(10_000) { Country("HELLO WORLD") }
        }
        memorizing("a big list of big objects") {
            List(10_000) { TestMemorable.construct() }
        }
        memorizing("a small list of small lists of ints") {
            List(10) { x ->
                List(10) { y ->
                    x * y
                }
            }
        }
        memorizing("a big list of small lists of ints") {
            List(1000) { x ->
                List(10) { y ->
                    x * y
                }
            }
        }
        memorizing("a big list of big lists of ints") {
            List(1000) { x ->
                List(1000) { y ->
                    x * y
                }
            }
        }
        memorizing("a small list of big lists of ints") {
            List(10) { x ->
                List(1000) { y ->
                    x * y
                }
            }
        }

        memorizing("a small list of small lists of objects") {
            List(10) { _ ->
                List(10) { _ ->
                    Country("Madagaskar")
                }
            }
        }
        memorizing("a big list of small lists of objects") {
            List(1000) { _ ->
                List(10) { _ ->
                    Country("Madagaskar")
                }
            }
        }
        memorizing("a big list of big lists of objects") {
            List(1000) { _ ->
                List(1000) { _ ->
                    Country("Madagaskar")
                }
            }
        }
        memorizing("a small list of big lists of objects") {
            List(10) { _ ->
                List(1000) { _ ->
                    Country("Madagaskar")
                }
            }
        }
    }
}

fun viewListMemorizationResults() {
    markdown.view("list memorization", currentTags, results.resolve("list memorization.html"))
}

fun main(args: Array<String>) {
    profileListMemorization()
}