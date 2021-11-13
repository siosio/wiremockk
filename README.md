# wiremockk
Kotlin DSL library for [WireMock](https://github.com/wiremock/wiremock)

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
}
```
