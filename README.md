# wiremockk
Kotlin DSL library for [WireMock](https://github.com/wiremock/wiremock)

![build](https://github.com/siosio/wiremockk/actions/workflows/build.yml/badge.svg)

## register example
### get
```kotlin
import com.github.siosio.wiremockk.register

wireMock.register {
    method = RequestMethod.GET
    url = "dummy"
    headers {
        contentType("application/json")
        set("x-test", "true")
    }
    
    response {
        headers {
            contentType("application/json")
            set("x-test", "test")
            set("x-test", "test2")
        }
    }
}
```
