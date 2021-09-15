package com.github.jackson.oauth2client.user

import javax.persistence.*

@Entity
@Table(name = "group_permission")
data class GroupPermission(
  @Id
  @Column(name = "id")
  var id: Long?,

  @ManyToOne(optional = false)
  @JoinColumn(name = "group_id")
  var group: Group,

  @ManyToOne(optional = false)
  @JoinColumn(name = "permission_id")
  var permission: Permission
)