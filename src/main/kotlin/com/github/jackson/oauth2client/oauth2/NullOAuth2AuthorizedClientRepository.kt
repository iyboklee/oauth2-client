package com.github.jackson.oauth2client.oauth2

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class NullOAuth2AuthorizedClientRepository : OAuth2AuthorizedClientRepository {

  override fun <T : OAuth2AuthorizedClient?> loadAuthorizedClient(
    clientRegistrationId: String,
    principal: Authentication,
    request: HttpServletRequest
  ): T? = null

  override fun saveAuthorizedClient(
    authorizedClient: OAuth2AuthorizedClient,
    principal: Authentication,
    request: HttpServletRequest,
    response: HttpServletResponse
  ) {
  }

  override fun removeAuthorizedClient(
    clientRegistrationId: String,
    principal: Authentication,
    request: HttpServletRequest,
    response: HttpServletResponse
  ) {
  }

}