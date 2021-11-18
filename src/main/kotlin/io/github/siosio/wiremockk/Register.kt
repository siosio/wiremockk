package io.github.siosio.wiremockk

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.stubbing.StubMapping

fun WireMock.register(init: StubMappingDsl.() -> Unit) {
    this.register(StubMappingDsl().apply(init).build())
}

class StubMappingDsl {
    private val requestDsl: RequestDsl = RequestDsl()
    private val responseDsl: ResponseDsl = ResponseDsl()

    fun request(init: RequestDsl.() -> Unit) {
        requestDsl.apply(init)
    }

    fun response(init: ResponseDsl.() -> Unit) {
        responseDsl.apply(init)
    }

    fun build(): StubMapping {
        val stubMapping = StubMapping().apply {
            request = requestDsl.build()
            response = responseDsl.build()
        }
        return stubMapping
    }
}
