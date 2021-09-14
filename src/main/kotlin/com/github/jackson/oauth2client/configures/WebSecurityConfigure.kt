package com.github.jackson.oauth2client.configures

import com.github.jackson.oauth2client.jwt.Jwt
import com.github.jackson.oauth2client.jwt.JwtAuthenticationTokenFilter
import com.github.jackson.oauth2client.oauth2.HttpCookieOAuth2AuthorizationRequestRepository
import com.github.jackson.oauth2client.oauth2.OAuth2AuthenticationSuccessHandler
import com.github.jackson.oauth2client.user.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.web.context.SecurityContextPersistenceFilter

@Configuration
@EnableWebSecurity
class WebSecurityConfigure : WebSecurityConfigurerAdapter() {

    @Bean
    fun jwt(configure: JwtConfigure): Jwt = Jwt(configure.secretKey!!, configure.issuer, configure.expirySeconds)

    @Bean
    fun jwtAuthenticationTokenFilter(jwt: Jwt, configure: JwtConfigure): JwtAuthenticationTokenFilter =
        JwtAuthenticationTokenFilter(jwt, configure.headerKey)

    @Bean
    fun authorizationRequestRepository(): AuthorizationRequestRepository<OAuth2AuthorizationRequest> =
        HttpCookieOAuth2AuthorizationRequestRepository()

    @Bean
    fun oauth2AuthenticationSuccessHandler(jwt: Jwt, userService: UserService): OAuth2AuthenticationSuccessHandler =
        OAuth2AuthenticationSuccessHandler(jwt, userService)

    @Bean
    fun authorizedClientService(
        jdbcOperations: JdbcOperations,
        clientRegistrationRepository: ClientRegistrationRepository
    ): OAuth2AuthorizedClientService =
        JdbcOAuth2AuthorizedClientService(jdbcOperations, clientRegistrationRepository)

    @Bean
    fun authorizedClientRepository(
        authorizedClientService: OAuth2AuthorizedClientService
    ): AuthenticatedPrincipalOAuth2AuthorizedClientRepository =
        AuthenticatedPrincipalOAuth2AuthorizedClientRepository(
            authorizedClientService
        )

    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
                .antMatchers("/api/user/me").hasAnyRole("USER")
                .anyRequest().permitAll()
            .and()
            .formLogin()
                .disable()
            .csrf()
                .disable()
            .headers()
                .disable()
            .httpBasic()
                .disable()
            .rememberMe()
                .disable()
            .logout()
                .disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .oauth2Login()
                .authorizationEndpoint()
                    .authorizationRequestRepository(authorizationRequestRepository())
                    .and()
                .successHandler(applicationContext.getBean(OAuth2AuthenticationSuccessHandler::class.java))
                .authorizedClientRepository(applicationContext.getBean(AuthenticatedPrincipalOAuth2AuthorizedClientRepository::class.java))
                .and()
            http.addFilterAfter(
                applicationContext.getBean(JwtAuthenticationTokenFilter::class.java),
                SecurityContextPersistenceFilter::class.java
            )
    }

}