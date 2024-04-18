package com.group.libraryapp.domin.book

import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Long> {

    fun findByName(bookName: String): Book?
}