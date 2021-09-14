package com.github.jackson.oauth2client.user

import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    val userRepository: UserRepository,
    val groupRepository: GroupRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    fun findByUsername(username: String): User? = userRepository.findByUsername(username)

    @Transactional(readOnly = true)
    fun findByProviderAndProviderId(provider: String, providerId: String): User? =
        userRepository.findByProviderAndProviderId(provider, providerId)

    @Transactional
    fun join(oauth2User: OAuth2User, authorizedClientRegistrationId: String): User {
        val providerId = oauth2User.name
        val user: User? = findByProviderAndProviderId(authorizedClientRegistrationId, providerId)
        return if (user == null) {
            @Suppress("UNCHECKED_CAST")
            val properties = oauth2User.attributes["properties"] as Map<String, Any>
            val nickname = properties["nickname"] as String
            val profileImage = properties["profile_image"] as String?
            val group = groupRepository.findByName("USER_GROUP")
                ?: throw IllegalStateException("Could not found group for USER_GROUP")
            userRepository.save(
                User(
                    id = null,
                    username = nickname,
                    provider = authorizedClientRegistrationId,
                    providerId = providerId,
                    profileImage = profileImage,
                    group = group
                )
            )
        } else {
            log.warn("Already exists: $user for (provider: ${authorizedClientRegistrationId}, providerId: ${providerId})")
            user
        }
    }

}