package com.group.libraryapp.dto.book.request

import com.group.libraryapp.domin.book.BookType

class BookRequest(
    val name: String,
    val type: BookType,
)
