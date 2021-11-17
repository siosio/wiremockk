package com.github.siosio.wiremockk

import java.io.InputStream

fun readTextFileFromResourcePath(path: String): String {
    return openResource(path).use { stream ->
        stream.use {
            stream.reader(Charsets.UTF_8).readText()
        }
    }
}

fun readBinaryFromResourcePath(path: String): ByteArray {
    return openResource(path).use { stream ->
        stream.readAllBytes()
    }
}

private fun openResource(path: String): InputStream {
    val stream = Thread.currentThread().contextClassLoader
        .getResourceAsStream(path)
    checkNotNull(stream) { "resource is not found! path: $path" }
    return stream
}