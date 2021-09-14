package com.github.jackson.oauth2client.user

import com.github.jackson.oauth2client.jwt.JwtAuthentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserApis(
    val userService: UserService
) {
    @GetMapping("/me")
    fun me(@AuthenticationPrincipal authentication: JwtAuthentication): UserDto {
        val user: User = userService.findByUsername(authentication.username)
            ?: throw IllegalArgumentException("Could not found user for ${authentication.username}")
        return UserDto(user.username, user.group.name)
    }
}