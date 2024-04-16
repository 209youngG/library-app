package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domin.book.Book
import com.group.libraryapp.domin.user.User
import com.group.libraryapp.domin.user.loanhistory.UserLoanHistory
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import org.assertj.core.api.AssertionsForInterfaceTypes.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BookServiceTest @Autowired constructor(
    private val bookService: BookService,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {

    @AfterEach
    fun tearDown() {
        bookRepository.deleteAll()
        userRepository.deleteAll()
    }

    @DisplayName("책 정상 등록")
    @Test
    fun saveBookTest() {
        // Given
        val request = BookRequest("Spring Boot")

        // When
        bookService.saveBook(request)

        // Then
        val books = bookRepository.findAll()
        assertThat(books).hasSize(1)
        assertThat(books[0].name).isEqualTo("Spring Boot")
    }

    @DisplayName("책 정상 대여")
    @Test
    fun loanBookTest() {
        // Given
        bookRepository.save(Book("Spring Boot"))
        val savedUser = userRepository.save(User("이영균", null))
        val bookLoanRequest = BookLoanRequest("이영균", "Spring Boot")

        // When
        bookService.loanBook(bookLoanRequest)

        // Then
        val userLoanHistories = userLoanHistoryRepository.findAll()
        assertThat(userLoanHistories).hasSize(1)
        assertThat(userLoanHistories[0].bookName).isEqualTo("Spring Boot")
        assertThat(userLoanHistories[0].user.id).isEqualTo(savedUser.id)
        assertThat(userLoanHistories[0].isReturn).isFalse()
    }
    @DisplayName("책 대여 실패, 대출이 이미 되어있으면, 신규 대출 불가")
    @Test
    fun loanBookFailTest() {
        // Given
        bookRepository.save(Book("Spring Boot"))
        val savedUser = userRepository.save(User("이영균", null))
        userLoanHistoryRepository.save(
            UserLoanHistory(
                savedUser,
                "Spring Boot",
                false
            )
        )
        val bookLoanRequest = BookLoanRequest("이영균", "Spring Boot")

        // When & Then
        val message = assertThrows<IllegalArgumentException> {
            bookService.loanBook(bookLoanRequest)
        }.message
        assertThat(message).isEqualTo("진작 대출되어 있는 책입니다")
    }

    @DisplayName("책 정상 반납")
    @Test
    fun returnBookTest() {
        // Given
        bookRepository.save(Book("Spring Boot"))
        val savedUser = userRepository.save(User("이영균", null))
        userLoanHistoryRepository.save(
            UserLoanHistory(
                savedUser,
                "Spring Boot",
                false
            )
        )
        val bookReturnRequest = BookReturnRequest("이영균", "Spring Boot")

        // When
        bookService.returnBook(bookReturnRequest)

        // Then
        val userLoanHistories = userLoanHistoryRepository.findAll()
        assertThat(userLoanHistories).hasSize(1)
        assertThat(userLoanHistories[0].isReturn).isTrue()

    }
}