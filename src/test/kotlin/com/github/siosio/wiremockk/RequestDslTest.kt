package com.github.siosio.wiremockk

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.github.tomakehurst.wiremock.matching.MultiValuePattern
import com.github.tomakehurst.wiremock.matching.UrlPathPattern
import com.github.tomakehurst.wiremock.matching.UrlPattern
import org.assertj.core.api.Assertions.*
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class RequestDslTest {

    val sut = RequestDsl()

    @Nested
    inner class Method {

        @Test
        internal fun defaultMethod() {
            val actual = sut.build()
            assertThat(actual.method)
                .isEqualTo(RequestMethod.ANY)
        }

        @Test
        internal fun setGetMethod() {
            sut.method = RequestMethod.GET
            val actual = sut.build()
            assertThat(actual.method)
                .isEqualTo(RequestMethod.GET)
        }

        @Test
        internal fun setPostMethod() {
            sut.method = RequestMethod.POST
            val actual = sut.build()
            assertThat(actual.method)
                .isEqualTo(RequestMethod.POST)
        }

        @Test
        internal fun setPutMethod() {
            sut.method = RequestMethod.PUT
            val actual = sut.build()
            assertThat(actual.method)
                .isEqualTo(RequestMethod.PUT)
        }

        @Test
        internal fun setDeleteMethod() {
            sut.method = RequestMethod.DELETE
            val actual = sut.build()
            assertThat(actual.method)
                .isEqualTo(RequestMethod.DELETE)
        }

        @Test
        internal fun setPatchMethod() {
            sut.method = RequestMethod.PATCH
            val actual = sut.build()
            assertThat(actual.method)
                .isEqualTo(RequestMethod.PATCH)
        }
    }

    @Nested
    inner class Url {
        @Test
        internal fun registerUrl() {
            sut.url = "/dummy?q=テスト"
            val actual = sut.build()

            assertThat(actual.url)
                .isEqualTo("/dummy?q=テスト")
        }

        @Test
        internal fun registerUrlPattern() {
            sut.urlPattern = WireMock.urlEqualTo("/dummy")
            val actual = sut.build()
            assertThat(actual.url)
                .isEqualTo("/dummy")
        }

        @Test
        internal fun registerUrlWithQueryParams() {
            sut.url("/dummy") {
                queryParam("name", "value")
                queryParam("name2", WireMock.equalTo("value1").and(WireMock.equalTo("value2")))
            }
            val actual = sut.build()
            SoftAssertions().apply {
                assertThat(actual.urlMatcher)
                    .isEqualTo(WireMock.urlPathEqualTo("/dummy"))
                assertThat(actual.queryParameters)
                    .extractingByKeys("name", "name2")
                    .containsExactly(
                        MultiValuePattern.of(WireMock.equalTo("value")),
                        MultiValuePattern.of(WireMock.equalTo("value1").and(WireMock.equalTo("value2"))),
                    )
            }.assertAll()
        }
    }

    @Nested
    inner class Header {
        @Test
        internal fun registerContentType() {
            sut.headers {
                contentType("application/json")
            }
            val actual = sut.build()
            assertThat(actual.headers)
                .extractingByKeys("Content-Type")
                .containsExactly(
                    MultiValuePattern.of(WireMock.equalTo("application/json")),
                )
        }

        @Test
        internal fun registerContentTypePattern() {
            sut.headers {
                contentType(WireMock.matching(".+/json"))
            }
            val actual = sut.build()
            assertThat(actual.headers)
                .extractingByKeys("Content-Type")
                .containsExactly(
                    MultiValuePattern.of(WireMock.matching(".+/json")),
                )
        }

        @Test
        internal fun registerHeader() {
            sut.headers {
                header("name", "value")
            }
            val actual = sut.build()
            assertThat(actual.headers)
                .extractingByKeys("name")
                .containsExactly(MultiValuePattern.of(WireMock.equalTo("value")))
        }

        @Test
        internal fun registerHeaderPattern() {
            sut.headers {
                header("name", WireMock.equalTo("value"))
                header("name2", WireMock.containing("v"))
            }
            val actual = sut.build()
            assertThat(actual.headers)
                .extractingByKeys("name", "name2")
                .containsExactly(
                    MultiValuePattern.of(WireMock.equalTo("value")),
                    MultiValuePattern.of(WireMock.containing("v")),
                )
        }
    }

    @Nested
    inner class Cookie {
        @Test
        internal fun cookieString() {
            sut.cookies {
                cookie("name", "value")
            }
            val actual = sut.build()
            assertThat(actual.cookies["name"])
                .isEqualTo(WireMock.equalTo("value"))
        }

        @Test
        internal fun cookiePattern() {
            sut.cookies {
                cookie("name", WireMock.containing("value"))
            }
            val actual = sut.build()
            assertThat(actual.cookies["name"])
                .isEqualTo(WireMock.containing("value"))
        }
    }

    @Nested
    inner class Body {
        @Test
        internal fun registerJson() {
            // language=JSON
            val json = """{"test": 1}"""
            sut.body {
                json(json)
            }
            val actual = sut.build()
            assertThat(actual.bodyPatterns)
                .containsAll(listOf(WireMock.equalToJson(json)))
        }

        @Test
        internal fun registerJsonResource() {
            sut.body {
                jsonPath("data/test.json")
            }
            val actual = sut.build()
            assertThat(actual.bodyPatterns)
                .containsAll(listOf(WireMock.equalToJson("""{"test": "value"}""")))
        }

        @Test
        internal fun registerJsonIgnorePattern() {
            // language=JSON
            val json = """{"test": 1}"""
            sut.body {
                json(json, true, false)
            }
            val actual = sut.build()
            assertThat(actual.bodyPatterns)
                .containsAll(listOf(WireMock.equalToJson(json, true, false)))
        }

        @Test
        internal fun registerJsonResourceIgnorePattern() {
            sut.body {
                jsonPath("data/test.json", true, false)
            }
            val actual = sut.build()
            assertThat(actual.bodyPatterns)
                .containsAll(listOf(WireMock.equalToJson("""{"test": "value"}""", true, false)))
        }
    }
}