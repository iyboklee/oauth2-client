package com.github.jackson.oauth2client.user

import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    val passwordEncoder: PasswordEncoder,
    val userRepository: UserRepository
) {
    @Transactional(readOnly = true)
    fun findByUsername(username: String): User? = userRepository.findByUsername(username)

    @Transactional(readOnly = true)
    fun login(principal: String, credentials: String): User? {
        val user: User = findByUsername(principal)
            ?: throw UsernameNotFoundException("Could not found user for $principal")
        user.checkPassword(passwordEncoder, credentials)
        return user
    }
}