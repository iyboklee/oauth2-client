package com.github.jackson.oauth2client.configures

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
@ConfigurationProperties(prefix = "jwt")
class JwtConfigure {
    var secretKey: String? = null
    var headerKey: String = "Authentication"
    var issuer: String = "Dunamu"
    var expirySeconds: Long = TimeUnit.HOURS.toSeconds(2)
}