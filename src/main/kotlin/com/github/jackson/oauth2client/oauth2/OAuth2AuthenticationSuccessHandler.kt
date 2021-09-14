package com.github.jackson.oauth2client.oauth2

import com.github.jackson.oauth2client.jwt.Jwt
import com.github.jackson.oauth2client.user.User
import com.github.jackson.oauth2client.user.UserService
import io.jsonwebtoken.Jwts
import org.apache.logging.log4j.LogManager
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class OAuth2AuthenticationSuccessHandler(
    private val jwt: Jwt,
    private val userService: UserService
) : SavedRequestAwareAuthenticationSuccessHandler() {

    private val log = LogManager.getLogger(javaClass)

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        // OAuth2LoginAuthenticationProvider를 통해 인증됨
        // ROLE_USER 권한은 어디서 올까?
        // DefaultOAuth2UserService.loadUser 구현에서 OAuth2UserAuthority 권한 생성 (OAuth2UserAuthority 객체는 ROLE_USER를 기본으로 지님)
        if (authentication is OAuth2AuthenticationToken) {
            val principal: OAuth2User = authentication.principal
            val registrationId = authentication.authorizedClientRegistrationId
            // 미가입 사용자라면 가입 처리함
            val user: User = processUserOAuth2UserJoin(principal, registrationId)
            val loginSuccessJson = generateLoginSuccessJson(user)
            response.contentType = "application/json;charset=UTF-8"
            response.setContentLength(loginSuccessJson.toByteArray().size)
            response.writer.write(loginSuccessJson)
        } else {
            super.onAuthenticationSuccess(request, response, authentication)
        }
    }

    private fun processUserOAuth2UserJoin(oAuth2User: OAuth2User, registrationId: String): User =
        userService.join(oAuth2User, registrationId)

    private fun generateLoginSuccessJson(user: User): String {
        val token: String = jwt.sign(
            Jwts.claims(
                mapOf(
                    "uid" to user.id,
                    "username" to user.username,
                    "roles" to arrayOf("ROLE_USER")
                )
            )
        )
        log.debug("Jwt($token) created for oauth2-user(${user.id})")
        return """
            {
                "token": "${token},
                "username": "${user.username}",
                "group": "${user.group.name}"
            }
        """.trimIndent()
    }

}