package io.github.siosio.wiremockk

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.RequestMethod
import io.github.rybalkinsd.kohttp.dsl.httpPost
import io.github.rybalkinsd.kohttp.ext.url
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
class VerifyTest {
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
    internal fun verifyGet() {
        wireMock.register {
            request {
                method = RequestMethod.POST
                url = "/test"
            }
            response {
                status = 200
                headers {
                    contentType("text/plain")
                }
                body {
                    string("messate")
                }
            }
        }

        val response = httpPost {
            url("http://localhost:${container.getMappedPort(8080)}/test")
            header {
                "Content-Type" to "application/json"
            }
            body {
                string("""{"test":"value"}""")
            }
        }
        Assertions.assertThat(response.code())
            .isEqualTo(200)

        wireMock.verify {
            url = "/test"
            method = RequestMethod.POST
            headers {
                header("Content-Type", "application/json")
            }
            body {
                json("""{"test":"value"}""")
            }
        }
    }
}