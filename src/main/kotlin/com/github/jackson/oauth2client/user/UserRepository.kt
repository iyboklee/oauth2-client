package com.github.jackson.oauth2client.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<User, Long> {

    @Query("""
       select 
            u 
        from 
            User u join fetch u.group g 
            left join fetch g.permissions gp 
            join fetch gp.permission 
        where 
            u.username = :username
    """)
    fun findByUsername(username: String): User?

}