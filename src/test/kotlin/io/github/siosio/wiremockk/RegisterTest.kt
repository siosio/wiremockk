package io.github.siosio.wiremockk

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.RequestMethod
import io.github.rybalkinsd.kohttp.dsl.httpGet
import io.github.rybalkinsd.kohttp.dsl.httpPost
import io.github.rybalkinsd.kohttp.ext.url
import net.javacrumbs.jsonunit.assertj.assertThatJson
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
    internal fun simpleGet() {
        wireMock.register {
            request {
                method = RequestMethod.GET
                url = "/test"
            }
            response {
                status = 200
                headers {
                    contentType("application/json")
                    header("x-test", "true")
                }
                body {
                    resourcePath = "data/test.json"
                }
            }
        }

        val response = httpGet {
            url("http://localhost:${container.getMappedPort(8080)}/test")
        }

        SoftAssertions().apply {
            assertThat(response.code())
                .isEqualTo(200)
            assertThat(response.header("content-type"))
                .isEqualTo("application/json")
            assertThat(response.header("x-test"))
                .isEqualTo("true")
        }.assertAll()
        assertThatJson(response.body()?.string()!!)
            .isEqualTo("""{"test":"value"}""")
    }

    @Test
    internal fun simplePost() {
        wireMock.register {
            request {
                method = RequestMethod.POST
                url = "/users"
                body {
                    // language=json
                    json(
                        """
                        {
                          "user": {
                            "name": "siosio"
                          }
                        }
                    """.trimIndent(), false, true)
                }
            }
            response {
                status = 201
                headers {
                    header("location", "users/1")
                }
            }
        }

        val response = httpPost {
            url("http://localhost:${container.getMappedPort(8080)}/users")
            body("application/json") {
                // language=JSON
                string(
                    """
                    {"user": {"name": "siosio", "mailAddress": "test@example.com"}}
                """.trimIndent())
            }
        }
        SoftAssertions().apply {
            assertThat(response.code())
                .isEqualTo(201)
            assertThat(response.header("location"))
                .isEqualTo("users/1")
        }.assertAll()
    }
}