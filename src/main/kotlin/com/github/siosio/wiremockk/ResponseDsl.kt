package com.github.siosio.wiremockk

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.http.ResponseDefinition

class ResponseDsl {
    var status: Int = 200
    private val headers = ResponseHeaderDsl()
    private val body = ResponseBodyDsl()

    fun headers(init: ResponseHeaderDsl.() -> Unit) {
        headers.apply(init)
    }

    fun body(init: ResponseBodyDsl.() -> Unit) {
        body.apply(init)
    }

    internal fun build(): ResponseDefinition {
        return status(status)
            .apply {
                headers.build(this)
                body.build(this)
            }.build()
    }
}

class ResponseHeaderDsl {
    private val headers = mutableListOf<Pair<String, Array<String>>>()

    fun contentType(value: String) {
        headers.add("Content-Type" to arrayOf(value))
    }

    fun header(name: String, vararg values: String) {
        headers.add(name to arrayOf(*values))
    }

    internal fun build(builder: ResponseDefinitionBuilder) {
        headers
            .forEach { (name, values) ->
                builder.withHeader(name, *values)
            }
    }
}

class ResponseBodyDsl {
    var resourcePath: String? = null
    var bodyString: String? = null

    internal fun build(builder: ResponseDefinitionBuilder) {
        when {
            bodyString != null -> {
                builder.withBody(bodyString)
            }
            resourcePath != null -> {
                builder.withBody(readBinaryFromResourcePath(checkNotNull(resourcePath)))
            }
        }
    }
}