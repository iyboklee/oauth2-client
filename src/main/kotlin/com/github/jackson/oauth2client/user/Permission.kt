package com.github.jackson.oauth2client.user

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "permissions")
data class Permission(
    @Id
    @Column(name = "id")
    var id: Long?,

    @Column(name = "name")
    var name: String
)