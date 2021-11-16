package com.github.siosio.wiremockk

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.http.ResponseDefinition
import java.lang.Thread.*

class ResponseDsl {
    var status: Int = 200
    val headers = ResponseHeaderDsl()
    val body = ResponseBodyDsl()

    fun headers(init: ResponseHeaderDsl.() -> Unit) {
        headers.apply(init)
    }

    fun body(init: ResponseBodyDsl.() -> Unit) {
        body.apply(init)
    }

    fun build(): ResponseDefinition {
        return status(status)
            .apply {
                headers.build(this)
                body.build(this)
            }.build()
    }
}

class ResponseHeaderDsl {
    val headers = mutableListOf<Pair<String, Array<String>>>()

    fun contentType(value: String) {
        headers.add("Content-Type" to arrayOf(value))
    }

    fun set(name: String, vararg values: String) {
        headers.add(name to arrayOf(*values))
    }

    fun build(builder: ResponseDefinitionBuilder) {
        headers
            .forEach { (name, values) ->
                builder.withHeader(name, *values)
            }
    }
}

class ResponseBodyDsl {
    var resourcePath: String? = null
    var filePath: String? = null
    var bodyString: String? = null

    fun build(builder: ResponseDefinitionBuilder) {
        when {
            bodyString != null -> {
                builder.withBody(bodyString)
            }
            resourcePath != null -> {
                val bytes = currentThread().contextClassLoader
                    .getResourceAsStream(resourcePath)
                    .readAllBytes()
                builder.withBody(bytes)
            }
        }
    }
}