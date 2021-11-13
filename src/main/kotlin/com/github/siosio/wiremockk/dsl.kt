package com.github.siosio.wiremockk

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.github.tomakehurst.wiremock.matching.StringValuePattern
import com.github.tomakehurst.wiremock.stubbing.StubMapping

fun WireMock.register(init: MappingBuilderDsl.() -> Unit): StubMapping {
    return register(MappingBuilderDsl().apply(init).build())
}

class MappingBuilderDsl {
    var method: RequestMethod? = null
    var url: String? = null
    private val headers = HeadersDsl()

    fun headers(init: HeadersDsl.() -> Unit) {
        headers.apply(init)
    }

    internal fun build(): MappingBuilder {
        checkNotNull(url) { "must be set url" }
        return when (method) {
            RequestMethod.GET -> get(url)
            else -> throw IllegalStateException("must be set method")
        }.apply {
            headers.headers.forEach { (name, value) ->
                withHeader(name, equalTo(value))
            }
            headers.headersPettern.forEach { (name, value) ->
                withHeader(name, value)
            }
        }
    }
}

class HeadersDsl {
    val headers = mutableListOf<Pair<String, String>>()
    val headersPettern = mutableListOf<Pair<String, StringValuePattern>>()

    fun contentType(value: String) {
        headers.add("Content-Type" to value)
    }

    fun contentType(value: StringValuePattern) {
        headersPettern.add("Content-Type" to value)
    }

    fun set(name: String, value: String) {
        headers.add(name to value)
    }

    fun set(name: String, value: StringValuePattern) {
        headersPettern.add(name to value)
    }
}