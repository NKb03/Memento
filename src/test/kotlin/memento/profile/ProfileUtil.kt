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

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import memento.Memento
import memento.Memorizer
import nikok.kprofile.api.*
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.file.Files.*
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

val projectDir = Paths.get("D:", "Aktive Projekte", "Memento")!!

val results = projectDir.resolve("results")!!

private val tmp = projectDir.resolve("tmp")

fun ProfileBody.memorizing(memorizer: Memorizer, description: String, creator: () -> Any) {
    val instance = creator.invoke()
    lateinit var memento: Memento
    memento = memorizingAndRemembering(description, memorizer, instance)
    serializingAndDeserializing(description, memento)
    javaSerializingAndDeserializing(description, instance)
}

private fun ProfileBody.javaSerializingAndDeserializing(description: String, obj: Any) {
    val path = createTempFile(tmp, "tmp", "ser")
    val desc = "java serializing $description"
    newOutputStream(path).use { os ->
        ObjectOutputStream(os).use { oos ->
            profile(desc) {
                oos.writeObject(obj)
            }
        }
    }
    val size = size(path)
    desc consumed size.of(MemoryConsumptionKind.disc)
    val deserialized = newInputStream(path, StandardOpenOption.DELETE_ON_CLOSE).use { os ->
        ObjectInputStream(os).use { oos ->
            lateinit var deserialized: Any
            profile("java deserializing $description") {
                deserialized = oos.readObject()
            }
            deserialized
        }
    }
    assertThat(deserialized, equalTo(obj))
}

private fun ProfileBody.serializingAndDeserializing(description: String, memento: Memento) {
    val path = createTempFile(tmp, "tmp", "ser")
    val desc = "serializing $description"
    newOutputStream(path).use {
        profile(desc) {
            memento.writeTo(it)
        }
    }
    val size = size(path)
    val memory = size.of(MemoryConsumptionKind.disc)
    desc consumed memory
    val deserialized = newInputStream(path, StandardOpenOption.DELETE_ON_CLOSE).use {
        profile("deserializing $description") {
            Memento.readFrom(it)
        }
    }
    assertThat(deserialized, equalTo(memento))
}

private fun ProfileBody.memorizingAndRemembering(description: String, memorizer: Memorizer, instance: Any): Memento {
    val memento = profile("memorizing $description") {
        memorizer.memorize(instance)
    }
    val remembered = profile("remembering $description") {
        memorizer.remember(memento)
    }
    assertThat(remembered, equalTo(instance))
    return memento
}

internal fun withMemorizer(memorizer: Memorizer, actions: MemorizerContext.() -> Unit) {
    val ctx = MemorizerContext(memorizer)
    ctx.actions()
}

private const val CHROME_PATH = """C:\Program Files (x86)\Google\Chrome\Application\chrome.exe"""

fun showResults(topic: String) {
    val path = results.resolve("$topic.html")
    path.toFile().deleteOnExit()
    markdown.view(topic, currentTags, path)
    Runtime.getRuntime().exec(arrayOf(CHROME_PATH, path.toString()))
}