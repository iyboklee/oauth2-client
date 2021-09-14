package com.github.jackson.oauth2client.jwt

import io.jsonwebtoken.Claims
import org.slf4j.LoggerFactory
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.GenericFilterBean
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationTokenFilter(
    private val jwt: Jwt,
    private val headerKey: String
) : GenericFilterBean() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        val request = req as HttpServletRequest
        val response = res as HttpServletResponse
        if (SecurityContextHolder.getContext().authentication == null) {
            val authenticationToken = obtainAuthorizationToken(request)
            if (authenticationToken != null) {
                try {
                    val decodedClaims = jwt.verify(authenticationToken).body
                    log.debug("jwt parse result: $decodedClaims")

                    val uid = decodedClaims["uid"] as? Number
                    val username = decodedClaims["username"] as? String
                    val authorities = obtainAuthorities(decodedClaims)
                    if (uid != null && username != null && authorities.isNotEmpty()) {
                        SecurityContextHolder.getContext().authentication = JwtAuthenticationToken(
                            principal = JwtAuthentication(uid.toLong(), username),
                            credentials = null,
                            authorities = authorities
                        ).apply {
                            details = WebAuthenticationDetailsSource().buildDetails(request)
                        }
                    }
                } catch (e: Exception) {
                    log.warn("Jwt processing failed: ${e.message}", e)
                }
            }
        } else {
            log.debug("SecurityContextHolder not populated with security token, as it already contained: ${SecurityContextHolder.getContext().authentication}")
        }
        chain.doFilter(request, response)
    }

    private fun obtainAuthorizationToken(request: HttpServletRequest): String? =
        request.getHeader(headerKey)?.let { token ->
            log.debug("Jwt authorization api detected: $token")
            try {
                val parts = URLDecoder
                    .decode(token, StandardCharsets.UTF_8)
                    .split(" ")
                if (parts.size == 2) {
                    val schema = parts[0]
                    val credentials = parts[1]
                    return if (BEARER.matcher(schema).matches()) credentials else null
                }
            } catch (e: Exception) {
                log.error("Unexpected error occurred: ${e.message}", e)
            }
            null
        }

    private fun obtainAuthorities(decodedClaims: Claims): List<GrantedAuthority> {
        val roles = decodedClaims["roles"] as? ArrayList<*>
        return if (roles == null || roles.size == 0) emptyList() else roles.map { role ->
            SimpleGrantedAuthority(role as String)
        }
    }

    companion object {
        val BEARER: Pattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE)
    }
}