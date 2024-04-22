package com.group.libraryapp.service.book

import com.group.libraryapp.domin.book.Book
import com.group.libraryapp.domin.book.BookRepository
import com.group.libraryapp.domin.user.UserRepository
import com.group.libraryapp.domin.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domin.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import com.group.libraryapp.dto.book.response.BookStatResponse
import com.group.libraryapp.util.fail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository
) {
    @Transactional
    fun saveBook(request: BookRequest) {
        val newBook = Book(request.name, request.type)
        bookRepository.save(newBook)
    }

    @Transactional
    fun loanBook(request: BookLoanRequest) {
        val book = bookRepository.findByName(request.bookName) ?: fail()
        require(
            userLoanHistoryRepository.findByBookNameAndStatus(
                request.bookName,
                UserLoanStatus.LOANED
            ) == null
        ) {
            throw IllegalArgumentException("진작 대출되어 있는 책입니다")
        }
        val user = userRepository.findByName(request.userName) ?: fail()
        user.loanBook(book)
    }

    @Transactional
    fun returnBook(request: BookReturnRequest) {
        val user = userRepository.findByName(request.userName) ?: fail()
        user.returnBook(request.bookName)
    }

    @Transactional(readOnly = true)
    fun countLoanedBook(): Int {
        return userLoanHistoryRepository.countByStatus(UserLoanStatus.LOANED).toInt()
    }

    fun getBookStatistics(): List<BookStatResponse> {
//        전체 book 리스트를 가지고와서 메모리 로딩 후
//        group by 하므로 네트워크, 어플리케이션 부하가 날 가능성이 있으므로 하기와 같이
//        처리하는게 좋지 않을까하는 강의자의 의견
//        return bookRepository.findAll()
//            .groupBy { book ->
//                book.type
//            }
//            .map { (type, books) ->
//                BookStatResponse(type, books.size)
//            }
        return bookRepository.getStats()
    }
}