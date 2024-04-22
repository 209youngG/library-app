package com.group.libraryapp.service.user

import com.group.libraryapp.domin.user.User
import com.group.libraryapp.domin.user.UserRepository
import com.group.libraryapp.domin.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domin.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domin.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import com.group.libraryapp.service.UserService
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest @Autowired constructor(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val userService: UserService,
    @Autowired private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {
    @AfterEach
    fun tearDown() {
        userRepository.deleteAll()
    }

    @DisplayName("유저 저장 성공")
    @Test
    fun saveUserTest() {
        // Given
        val userCreateRequest = UserCreateRequest("이영균", null)

        // When
        userService.saveUser(userCreateRequest)

        // Then
        val results = userRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo("이영균")
        assertThat(results[0].age).isNull()
    }

    @DisplayName("유저 조회 성공")
    @Test
    fun getUserTest() {
        // Given
        userRepository.saveAll(
            listOf(
                User("A", 20),
                User("B", null)
            )
        )

        // When
        val results = userService.getUsers()

        // Then
        assertThat(results).hasSize(2)
        assertThat(results).extracting("name").containsExactlyInAnyOrder("A", "B")
        assertThat(results).extracting("age").containsExactlyInAnyOrder(20, null)
    }

    @DisplayName("유저 수정 성공")
    @Test
    fun updateUserNameTest() {
        // Given
        val saveUser = userRepository.save(User("A", 20))
        val userUpdateRequest = UserUpdateRequest(saveUser.id!!, "B")

        // When
        userService.updateUserName(userUpdateRequest)

        // Then
        val result = userRepository.findAll()[0]
        assertThat(result.name).isEqualTo("B")
    }

    @DisplayName("유저 삭제 성공")
    @Test
    fun deleteUserTest() {
        // Given
        val saveUser = userRepository.save(User("A", 20))

        // When
        userService.deleteUser("A")

        // Then
        assertThat(userRepository.findAll().filter{
            user -> user.id!!.equals(saveUser.id)
        }).isEmpty()

    }

    @Test
    @DisplayName("대툴 기록이 없는 유저도 응답에 포함된다")
    fun getUserLoanHistoriesTest1() {
        // Given
        userRepository.save(User("A", null))

        // When
        val results = userService.getUserLoanHistories()

        // Then
        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo("A")
        assertThat(results[0].books).isEmpty()
    }

    @Test
    @DisplayName("대툴 기록이 않은 유저도 응답이 정상 동작")
    fun getUserLoanHistoriesTest2() {
        // Given
        val saveUser = userRepository.save(User("A", null))
        userLoanHistoryRepository.saveAll(
            listOf(
                UserLoanHistory.fixture(saveUser, "책1", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(saveUser, "책2", UserLoanStatus.LOANED),
                UserLoanHistory.fixture(saveUser, "책3", UserLoanStatus.RETURNED),
                UserLoanHistory.fixture(saveUser, "책4", UserLoanStatus.LOANED),

                )
        )

        // When
        val results = userService.getUserLoanHistories()

        // Then
        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo("A")
        assertThat(results[0].books).hasSize(4)
        assertThat(results[0].books).extracting("name")
            .containsExactlyInAnyOrder("책1", "책2", "책3", "책4")
        assertThat(results[0].books).extracting("isReturn")
            .containsExactlyInAnyOrder(false, false, true, false)
    }
}