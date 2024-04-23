package com.group.libraryapp.domin.user

interface UserRepositoryCustom {

    fun findAllWithHistories(): List<User>
}