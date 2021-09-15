package com.github.jackson.oauth2client.user

import javax.persistence.*

@Entity
@Table(name = "groups")
data class Group(
  @Id
  @Column(name = "id")
  var id: Long?,

  @Column(name = "name")
  var name: String,

  @OneToMany(mappedBy = "group")
  val permissions: List<GroupPermission> = emptyList()
)