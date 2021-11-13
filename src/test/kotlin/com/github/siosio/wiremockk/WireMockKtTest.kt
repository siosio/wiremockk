package com.github.siosio.wiremockk

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.github.tomakehurst.wiremock.matching.MultiValuePattern
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
internal class WireMockKtTest {

    companion object {
        @Container
        private val container: GenericContainer<Nothing> = GenericContainer<Nothing>(DockerImageName.parse("wiremock/wiremock:latest"))
            .withExposedPorts(8080)
    }

    private lateinit var wireMock: WireMock

    @BeforeEach
    internal fun setUp() {
        container.waitingFor(HttpWaitStrategy())
        wireMock = WireMock(container.getMappedPort(8080))
        wireMock.resetToDefaultMappings()
    }


    @Nested
    inner class Get {

        @Test
        internal fun register() {
            val actual = wireMock.register {
                method = RequestMethod.GET
                url = "dummy"
            }.request
            assertThat(actual.method)
                .isEqualTo(RequestMethod.GET)
        }

        @Test
        internal fun registerUrl() {
            val actual = wireMock.register {
                method = RequestMethod.GET
                url = "http://test.example.com"
            }.request
            assertThat(actual.url)
                .isEqualTo("http://test.example.com")
        }

        @Test
        internal fun registerHeaders() {
            val actual = wireMock.register {
                method = RequestMethod.GET
                url = "dummy"
                headers {
                    contentType("application/json")
                    set("x-test", "true")
                }
            }.request

            assertThat(actual.headers)
                .extractingByKeys("Content-Type", "x-test")
                .containsExactly(MultiValuePattern.of(equalTo("application/json")), MultiValuePattern.of(equalTo("true")))
        }

        @Test
        internal fun registerHeadersPettern() {
            val actual = wireMock.register {
                method = RequestMethod.GET
                headers {
                    url = "dummy"
                    contentType(containing("json"))
                    set("x-test", equalToIgnoreCase("TRUE"))
                }
            }.request

            assertThat(actual.headers)
                .extractingByKeys("Content-Type", "x-test")
                .containsExactly(MultiValuePattern.of(containing("json")), MultiValuePattern.of(equalToIgnoreCase("TRUE")))
        }
    }
}
