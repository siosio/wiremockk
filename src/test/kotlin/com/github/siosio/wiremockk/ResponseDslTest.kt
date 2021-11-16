package com.github.siosio.wiremockk

import org.assertj.core.api.Assertions
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
}