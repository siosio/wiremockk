package com.github.siosio.wiremockk

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.github.tomakehurst.wiremock.matching.AnythingPattern
import com.github.tomakehurst.wiremock.matching.RequestPattern
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder
import com.github.tomakehurst.wiremock.matching.StringValuePattern
import com.github.tomakehurst.wiremock.matching.UrlPattern

class RequestDsl {
    var method: RequestMethod = RequestMethod.ANY
    var urlPattern: UrlPattern = UrlPattern.ANY
    private val queryParam = QueryParamDsl()
    private val headers = RequestHeadersDsl()
    private val body = RequestBodyDsl()

    var url: String
        get() = this.urlPattern.pattern.value
        set(value) {
            urlPattern = WireMock.urlEqualTo(value)
        }

    fun url(url: String, init: QueryParamDsl.() -> Unit) {
        urlPattern = urlPathEqualTo(url)
        queryParam.apply(init)
    }

    fun headers(init: RequestHeadersDsl.() -> Unit) {
        headers.apply(init)
    }

    fun body(init: RequestBodyDsl.() -> Unit) {
        body.apply(init)
    }

    internal fun build(): RequestPattern {
        return when (method) {
            RequestMethod.ANY -> WireMock.anyRequestedFor(urlPattern)
            RequestMethod.GET -> WireMock.getRequestedFor(urlPattern)
            RequestMethod.POST -> WireMock.postRequestedFor(urlPattern)
            RequestMethod.PUT -> WireMock.putRequestedFor(urlPattern)
            RequestMethod.DELETE -> WireMock.deleteRequestedFor(urlPattern)
            RequestMethod.PATCH -> WireMock.patchRequestedFor(urlPattern)
            else -> throw IllegalStateException("unsupported method!")
        }.apply {
            queryParam.build(this)
            headers.build(this)
            body.build(this)
        }.build()
    }

}

class QueryParamDsl {
    private val queryParams = mutableListOf<Pair<String, StringValuePattern>>()

    fun queryParam(name: String, value: String) {
        this.queryParams.add(name to equalTo(value))
    }

    fun queryParam(name: String, valuePattern: StringValuePattern) {
        this.queryParams.add(name to valuePattern)
    }

    fun build(requestPatternBuilder: RequestPatternBuilder) {
        queryParams.forEach { (name, value) ->
            requestPatternBuilder.withQueryParam(name, value)
        }
    }

}

class RequestHeadersDsl {
    private val headers = mutableListOf<Pair<String, StringValuePattern>>()

    fun contentType(value: String) {
        headers.add("Content-Type" to equalTo(value))
    }

    fun contentType(value: StringValuePattern) {
        headers.add("Content-Type" to value)
    }

    fun header(name: String, value: String) {
        headers.add(name to equalTo(value))
    }

    fun header(name: String, value: StringValuePattern) {
        headers.add(name to value)
    }

    fun build(requestPatternBuilder: RequestPatternBuilder) {
        headers.forEach { (name, value) ->
            requestPatternBuilder.withHeader(name, value)
        }
    }
}

class RequestBodyDsl {
    private var bodyPatten: StringValuePattern = AnythingPattern()

    fun json(json: String) {
        bodyPatten = equalToJson(json)
    }

    fun json(json: String, ignoreArrayOrder: Boolean, ignoreExtraElements: Boolean) {
        bodyPatten = equalToJson(json, ignoreArrayOrder, ignoreExtraElements)
    }

    fun jsonPath(jsonResourcePath: String) {
        bodyPatten = equalToJson(readTextFileFromResourcePath(jsonResourcePath))
    }

    fun jsonPath(jsonResourcePath: String, ignoreArrayOrder: Boolean, ignoreExtraElements: Boolean) {
        bodyPatten = equalToJson(readTextFileFromResourcePath(jsonResourcePath), ignoreArrayOrder, ignoreExtraElements)
    }

    fun build(requestPatternBuilder: RequestPatternBuilder) {
        requestPatternBuilder.withRequestBody(bodyPatten)
    }
}