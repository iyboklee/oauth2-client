package com.github.jackson.oauth2client.jwt

import io.jsonwebtoken.Jwts
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

internal class JwtTest {

  private val jwt: Jwt = Jwt(
    secretKey = "EENY5W0eegTf1naQB2eDeyCLl5kRS2b8xa5c4qLdS0hmVjtbvo8tOyhPMcAmtPuQ",
    issuer = "Dunamu",
    expirySeconds = TimeUnit.HOURS.toSeconds(2)
  )

  private val claims = Jwts.claims(
    mapOf(
      "uid" to 2L,
      "username" to "admin",
      "roles" to arrayOf("ROLE_USER", "ROLE_ADMIN")
    )
  )

  @Test
  fun `JWT encode, decode 테스트`() {
    val token = jwt.sign(claims)
    assertThat(token).isNotNull
    println("Generated token: $token")
    val decodedClaims = jwt.verify(token).body
    assertThat((decodedClaims["uid"] as Number).toLong()).isEqualTo(claims["uid"])
    assertThat(decodedClaims["username"] as String).isEqualTo(claims["username"])
    assertThat(decodedClaims["roles"] as ArrayList<*>)
      .isNotEmpty
      .contains("ROLE_USER", "ROLE_ADMIN")
  }

}