package io.github.siosio.wiremockk

import net.javacrumbs.jsonunit.assertj.assertThatJson
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ResponseDslTest {

    val sut = ResponseDsl()

    @Nested
    inner class Status {
        @Test
        internal fun deafult() {
            val actual = sut.build()

            assertThat(actual.status)
                .isEqualTo(200)
        }

        @Test
        internal fun notFound() {
            sut.status = 404
            val actual = sut.build()
            assertThat(actual.status)
                .isEqualTo(404)
        }
    }

    @Nested
    inner class Header {

        @Test
        internal fun contentType() {
            sut.headers {
                contentType("application/json")
            }

            val actual = sut.build()
            assertThat(actual.headers.getHeader("Content-Type").firstValue())
                .isEqualTo("application/json")
        }

        @Test
        internal fun simpleHeader() {
            sut.headers {
                header("name", "value")
            }

            val actual = sut.build()
            assertThat(actual.headers.getHeader("name").firstValue())
                .isEqualTo("value")
        }

        @Test
        internal fun multipleValueHeader() {
            sut.headers {
                header("name", "value", "value2")
            }

            val actual = sut.build()
            assertThat(actual.headers.getHeader("name").values())
                .contains("value", "value2")
        }
    }

    @Nested
    inner class Body {
        @Test
        internal fun bodyFromText() {
            sut.body {
                // language=JSON
                bodyString = """
                    {
                      "key": "value"
                    }
                """.trimIndent()
            }

            val actual = sut.build()

            assertThatJson(actual.body)
                .isEqualTo("""{"key":"value"}""")
        }

        @Test
        internal fun bodyFromResourceFile() {
            sut.body {
                resourcePath = "data/test.json"
            }

            val actual = sut.build()
            assertThat(actual.byteBody.toString(Charsets.UTF_8))
                .isEqualTo("""{"test": "value"}""")
        }
    }
}