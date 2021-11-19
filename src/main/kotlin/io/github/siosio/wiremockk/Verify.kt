package io.github.siosio.wiremockk

import com.github.tomakehurst.wiremock.client.WireMock

fun WireMock.verify(init: RequestDsl.() -> Unit) {
    this.verify(1, init)
}

fun WireMock.verify(count: Int, init: RequestDsl.() -> Unit) {
    this.verifyThat(count, RequestDsl().apply(init).builder())
}
