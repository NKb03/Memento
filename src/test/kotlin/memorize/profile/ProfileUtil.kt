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

package memorize.profile

import memorize.Memento
import memorize.Memorizer
import nikok.kprofile.api.ProfileBody
import java.nio.file.Files
import java.nio.file.Path

fun ProfileBody.memorizing(memorizer: Memorizer, description: String, creator: () -> Any) {
    val instance = creator.invoke()
    profile("memorizing $description") {
        memorizer.memorize(instance)
    }
}

fun ProfileBody.serializing(description: String, path: Path, mementoCreator: () -> Memento) {

    val memento = mementoCreator.invoke()
    path.toFile().createNewFile()
    val os = Files.newOutputStream(path)
    profile("serializing $description") {
        memento.writeTo(os)
    }
    path.toFile().delete()
}

fun ProfileBody.serializing(description: String, path: Path, memorizer: Memorizer, objectCreator: () -> Any) {
    this.serializing(description, path) { memorizer.memorize(objectCreator.invoke()) }
}



internal fun withMemorizer(memorizer: Memorizer, actions: MemorizerContext.() -> Unit) {
    val ctx = MemorizerContext(memorizer)
    ctx.actions()
}