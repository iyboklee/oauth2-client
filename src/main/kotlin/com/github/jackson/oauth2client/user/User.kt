package com.github.jackson.oauth2client.user

import org.springframework.security.crypto.password.PasswordEncoder
import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(name = "id")
    var id: Long?,

    @Column(name = "username")
    var username: String,

    @Column(name = "passwd")
    var passwd: String,

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id")
    val group: Group
) {
    fun checkPassword(passwordEncoder: PasswordEncoder, credentials: String) {
        if (!passwordEncoder.matches(credentials, passwd)) {
            throw IllegalArgumentException("Bad credentials")
        }
    }
}