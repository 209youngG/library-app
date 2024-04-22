package com.group.libraryapp.service.book

import com.group.libraryapp.domin.book.Book
import com.group.libraryapp.domin.book.BookRepository
import com.group.libraryapp.domin.book.BookType
import com.group.libraryapp.domin.user.User
import com.group.libraryapp.domin.user.UserRepository
import com.group.libraryapp.domin.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domin.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domin.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import com.group.libraryapp.dto.book.response.BookStatResponse
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
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
        val request = BookRequest("Spring Boot", BookType.COMPUTER)

        // When
        bookService.saveBook(request)

        // Then
        val books = bookRepository.findAll()
        assertThat(books).hasSize(1)
        assertThat(books[0].name).isEqualTo("Spring Boot")
        assertThat(books[0].type).isEqualTo(BookType.COMPUTER)
    }

    @DisplayName("책 정상 대여")
    @Test
    fun loanBookTest() {
        // Given
        bookRepository.save(Book.fixture("Spring Boot"))
        val savedUser = userRepository.save(User("이영균", null))
        val bookLoanRequest = BookLoanRequest("이영균", "Spring Boot")

        // When
        bookService.loanBook(bookLoanRequest)

        // Then
        val userLoanHistories = userLoanHistoryRepository.findAll()
        assertThat(userLoanHistories).hasSize(1)
        assertThat(userLoanHistories[0].bookName).isEqualTo("Spring Boot")
        assertThat(userLoanHistories[0].user.id).isEqualTo(savedUser.id)
        assertThat(userLoanHistories[0].status).isEqualTo(UserLoanStatus.LOANED)
    }

    @DisplayName("책 대여 실패, 대출이 이미 되어있으면, 신규 대출 불가")
    @Test
    fun loanBookFailTest() {
        // Given
        bookRepository.save(Book.fixture("Spring Boot"))
        val savedUser = userRepository.save(User("이영균", null))
        userLoanHistoryRepository.save(
            UserLoanHistory.fixture(
                savedUser,
                "Spring Boot",
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
        bookRepository.save(Book.fixture("Spring Boot"))
        val savedUser = userRepository.save(User("이영균", null))
        userLoanHistoryRepository.save(
            UserLoanHistory.fixture(
                savedUser,
                "Spring Boot"
            )
        )
        val bookReturnRequest = BookReturnRequest("이영균", "Spring Boot")

        // When
        bookService.returnBook(bookReturnRequest)

        // Then
        val userLoanHistories = userLoanHistoryRepository.findAll()
        assertThat(userLoanHistories).hasSize(1)
        assertThat(userLoanHistories[0].status).isEqualTo(UserLoanStatus.RETURNED)
    }

    @Test
    @DisplayName("책 대여 권수를 정상 확인한다")
    fun countLoanedBookTest() {
        // Given
        val savedUser = userRepository.save(User("이영균", null))
        userLoanHistoryRepository.saveAll(
            listOf(
                UserLoanHistory.fixture(
                    savedUser,
                    "Spring Boot1"
                ),
                UserLoanHistory.fixture(
                    savedUser,
                    "Spring Boot2",
                    UserLoanStatus.RETURNED
                ),
            )
        )

        // When
        val result = bookService.countLoanedBook()

        // Then
        assertThat(result).isEqualTo(1)
    }

    @Test
    @DisplayName("분야별 책 권수를 정상 확인한다")
    fun getBookStatisticsTest() {
        // Given
        bookRepository.saveAll(
            listOf(
                Book.fixture("A", BookType.COMPUTER),
                Book.fixture("B", BookType.COMPUTER),
                Book.fixture("C", BookType.SCIENCE),
            )
        )

        // When
        val results = bookService.getBookStatistics()

        // Then
        assertThat(results).hasSize(2)
        assertCount(results, BookType.COMPUTER, 2L)
        assertCount(results, BookType.SCIENCE, 1L)
    }

    private fun assertCount(results: List<BookStatResponse>, type: BookType, count: Long) {
        assertThat(results.first { result -> result.type == type }.count).isEqualTo(count)
    }
}