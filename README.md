# wiremockk
Kotlin DSL library for [WireMock](https://github.com/wiremock/wiremock)

![build](https://github.com/siosio/wiremockk/actions/workflows/build.yml/badge.svg)

## register example
### match url pattern
```kotlin
import com.github.siosio.wiremockk.register

wireMock.register {
    request {
        method = RequestMethod.GET
        url = "/test"
    }
    response {
        status = 200
    }
}
```
