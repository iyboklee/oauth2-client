package com.github.jackson.oauth2client.jwt

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

data class JwtAuthentication(val userId: Long, val username: String)

class JwtAuthenticationToken : AbstractAuthenticationToken {

  private val principal: Any

  private var credentials: String?

  constructor(principal: String, credentials: String) : super(null) {
    super.setAuthenticated(false)
    this.principal = principal
    this.credentials = credentials
  }

  internal constructor(
    principal: Any,
    credentials: String?,
    authorities: List<GrantedAuthority>
  ) : super(authorities) {
    super.setAuthenticated(true)
    this.principal = principal
    this.credentials = credentials
  }

  override fun getPrincipal(): Any = principal

  override fun getCredentials(): String? = credentials

  override fun setAuthenticated(authenticated: Boolean) {
    require(!isAuthenticated) {
      "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead"
    }
    super.setAuthenticated(false)
  }

  override fun eraseCredentials() {
    super.eraseCredentials()
    credentials = null
  }

}