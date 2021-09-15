package com.github.jackson.oauth2client.oauth2

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.util.SerializationUtils
import org.springframework.web.util.WebUtils
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class HttpCookieOAuth2AuthorizationRequestRepository(
  private val cookieExpireSeconds: Int = 180
) : AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

  override fun loadAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest? =
    getCookie(request)?.let { cookie -> getOAuth2AuthorizationRequest(cookie) }

  override fun saveAuthorizationRequest(
    authorizationRequest: OAuth2AuthorizationRequest?,
    request: HttpServletRequest,
    response: HttpServletResponse
  ) {
    if (authorizationRequest == null) {
      WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)?.let { cookie -> clear(cookie, response) }
    } else {
      val value = Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(authorizationRequest))
      val cookie = Cookie(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, value)
      cookie.path = "/"
      cookie.isHttpOnly = true
      cookie.maxAge = cookieExpireSeconds
      response.addCookie(cookie)
    }
  }

  override fun removeAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest? =
    loadAuthorizationRequest(request)

  override fun removeAuthorizationRequest(
    request: HttpServletRequest,
    response: HttpServletResponse
  ): OAuth2AuthorizationRequest? = getCookie(request)?.let { cookie ->
    val oauth2AuthorizationRequest = getOAuth2AuthorizationRequest(cookie)
    clear(cookie, response)
    oauth2AuthorizationRequest
  }

  private fun getCookie(request: HttpServletRequest): Cookie? =
    WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)

  private fun getOAuth2AuthorizationRequest(cookie: Cookie): OAuth2AuthorizationRequest? =
    SerializationUtils.deserialize(
      Base64.getUrlDecoder().decode(cookie.value)
    ) as? OAuth2AuthorizationRequest

  private fun clear(cookie: Cookie, response: HttpServletResponse) {
    cookie.value = ""
    cookie.path = "/"
    cookie.maxAge = 0
    response.addCookie(cookie)
  }

  companion object {
    private val OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request"
  }

}