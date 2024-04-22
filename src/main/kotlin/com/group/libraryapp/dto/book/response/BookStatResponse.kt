package com.group.libraryapp.dto.book.response

import com.group.libraryapp.domin.book.BookType

class BookStatResponse(
    val type: BookType,
    val count: Long,
) {

}