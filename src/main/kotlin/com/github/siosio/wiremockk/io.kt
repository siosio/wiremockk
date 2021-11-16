package com.github.siosio.wiremockk

fun readTextFileFromResourcePath(path: String): String {
    val stream = Thread.currentThread().contextClassLoader
        .getResourceAsStream(path)
    checkNotNull(stream) { "resource is not found! path: $path" }
    return stream.reader(Charsets.UTF_8).readText()
}