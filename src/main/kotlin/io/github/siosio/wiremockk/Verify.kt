package io.github.siosio.wiremockk

import com.github.tomakehurst.wiremock.client.WireMock

fun WireMock.verify(init: RequestDsl.() -> Unit) {
    this.verifyThat(RequestDsl().apply(init).builder())
}
