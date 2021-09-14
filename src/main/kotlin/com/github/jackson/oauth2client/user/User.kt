package com.github.jackson.oauth2client.user

import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(name = "username")
    var username: String,

    @Column(name = "provider")
    var provider: String,

    @Column(name = "provider_id")
    var providerId: String,

    @Column(name = "profile_image")
    var profileImage: String?,

    @ManyToOne(optional = false)
    @JoinColumn(name = "group_id")
    var group: Group
) {
    override fun toString(): String {
        return "User(id=$id, username='$username', provider='$provider', providerId='$providerId', profileImage=$profileImage)"
    }
}