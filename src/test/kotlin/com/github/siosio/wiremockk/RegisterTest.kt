package com.github.siosio.wiremockk

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.RequestMethod
import io.github.rybalkinsd.kohttp.dsl.httpGet
import io.github.rybalkinsd.kohttp.ext.httpGet
import io.github.rybalkinsd.kohttp.ext.url
import org.assertj.core.api.Assertions.*
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
internal class RegisterTest {

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
        wireMock.resetMappings()
    }

    @Test
    internal fun urlOnly() {
        wireMock.register {
            request {
                method = RequestMethod.GET
                url = "/test"
            }
            response {
                status = 200

                headers {
                    header("x-test", "true")
                }
            }
        }

        val response = httpGet {
            url("http://localhost:${container.getMappedPort(8080)}/test")
        }

        SoftAssertions().apply {
            assertThat(response.code())
                .isEqualTo(200)

            assertThat(response.header("x-test"))
                .isEqualTo("true")
        }.assertAll()
    }

    @Test
    internal fun withQueryParam() {
        wireMock.register {
            request {
                method = RequestMethod.GET
                url("/test") {
                    queryParam("q", "value")
                }
            }
            response {
                status = 500
            }
        }

        val response = httpGet {
            url("http://localhost:${container.getMappedPort(8080)}/test")
            param {
                "q" to "value"
            }
        }
        assertThat(response.code())
            .isEqualTo(500)
    }
}