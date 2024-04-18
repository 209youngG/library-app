package com.group.libraryapp.domin.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByName(name: String): User?
}