package com.github.jackson.oauth2client.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.oauth2.core.user.OAuth2User

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UserServiceTest {

  @Autowired
  lateinit var userService: UserService

  @Test
  fun `사용자 추가 테스트`() {
    val user1 = userService.join(
      oauth2User = mockOAuth2User("1", getAttributes("A", "image_url")),
      authorizedClientRegistrationId = "kakao"
    )
    assertThat(user1).isNotNull
    val user2 = userService.join(
      oauth2User = mockOAuth2User("2", getAttributes("B", null)),
      authorizedClientRegistrationId = "kakao"
    )
    assertThat(user2).isNotNull

    // 기존 사용자 정보를 추가해본다
    val user3 = userService.join(
      oauth2User = mockOAuth2User("1", getAttributes("A", "image_url")),
      authorizedClientRegistrationId = "kakao"
    )
    assertThat(user3).isNotNull
    assertThat(user3.id).isEqualTo(user1.id)
    assertThat(user3.username).isEqualTo(user1.username)
    assertThat(user3.provider).isEqualTo(user1.provider)
    assertThat(user3.providerId).isEqualTo(user1.providerId)
    assertThat(user3.profileImage).isEqualTo(user1.profileImage)
  }

  fun getAttributes(nickname: String, profileImage: String?) = mapOf(
    "properties" to mapOf(
      "nickname" to nickname,
      "profile_image" to profileImage
    )
  )

  fun mockOAuth2User(name: String, attributes: Map<String, Any>): OAuth2User = mock(OAuth2User::class.java)
    .apply {
      given(this.name).willReturn(name)
      given(this.attributes).willReturn(attributes)
    }

}