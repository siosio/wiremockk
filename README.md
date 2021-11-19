# wiremockk
Kotlin DSL library for [WireMock](https://github.com/wiremock/wiremock)

![build](https://github.com/siosio/wiremockk/actions/workflows/build.yml/badge.svg)

## register example
### simple get register
```kotlin
import io.github.siosio.wiremockk.register

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
```

### simple post register
```kotlin
import io.github.siosio.wiremockk.register

wireMock.register {
    request {
        method = RequestMethod.POST
        url = "/users"
        body {
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
```

## verify
```kotlin
import io.github.siosio.wiremockk.verify

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
```
