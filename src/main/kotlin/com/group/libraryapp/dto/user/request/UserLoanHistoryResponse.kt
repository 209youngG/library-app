package com.group.libraryapp.dto.user.request

data class UserLoanHistoryResponse (
    val name: String,
    val books: List<BookHistoryresponse>,
){
}

data class BookHistoryresponse (
    val name: String,
    val isReturn: Boolean,
){

}
