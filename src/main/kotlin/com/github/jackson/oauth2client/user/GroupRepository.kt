package com.github.jackson.oauth2client.user

import org.springframework.data.jpa.repository.JpaRepository

interface GroupRepository : JpaRepository<Group, Long> {

    fun findByName(name: String): Group?

}