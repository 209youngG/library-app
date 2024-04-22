package com.group.libraryapp.service

import com.group.libraryapp.domin.user.User
import com.group.libraryapp.domin.user.UserRepository
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserLoanHistoryResponse
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import com.group.libraryapp.dto.user.response.UserResponse
import com.group.libraryapp.util.fail
import com.group.libraryapp.util.findByIdOrThrow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


/**
 * Class 'UserService' could be implicitly subclassed and must not be final
 * Methods annotated with '@Transactional' must be overridable
 * Spirng에서 AOP를 적용할 때 런타임시 프록시를 생성한다. 이때 인터페이스 기반은 JDK Dynamic Proxy로 클래스 기반은 CGLIB를 사용
 * 'Spring @Transactional 기능제공 방식'을 보면 Spring Boot(스프링 3.2 버전부터)는 기본적으로 CGLIB 사용
 * CGLIB를 적용할 클래스는 final 메소드가 들어있거나, final 클래스면 안된다. 또한 private 접근자로 된 메소드도 상속이 불가하므로 적용되지 않는다
 * final 키워드가 클래스에 있으면 상속이 불가능
 * -> 코틀린은 모든 클래스와 매서드는 기본이 final이기 때문에 상기와 같은 오류가 발생함
 * -> 상기 오류를 처리 하기 위해 클래스와 매소드를 하기와 같이 open을 붙여주거나
 *      open class UserService(
 *          private val userRepository: UserRepository
 *      ) {
 *
 *          @Transactional
 *          open fun saveUser() {
 *
 *          }
 * -> gradle에 하기 플러그 인 추가 (스프링에서 사용하는 특정 어노테이션 사용시 all-open 클래스로 만들어주는 플러그인)
 *      plugins {
 *          id 'org.jetbrains.kotlin.plugin.spring' version '1.6.21'
 *      }
 *      - all-open 클래스로 만들어주는 어노테이션
 *          1. @Component와 @Component를 상속받는 어노테이션(@Configuration, @Controller, @RestController, @Service, @Repository)
 *          2. @Async
 *          3. @Transactional
 *          3. @Cacheable
 *          4. @SpringBootTest
 *      - 이 플러그인이 JPA에서 사용되는 @Entity, Embeddable, @MappedSuperclass는 all-open으로 만들어주지 않기 때문에 all-open 플러그인을 적용함을 명시해야함
 *          allOpen {
 *              annotation("javax.persistence.Entity")
 *              annotation("javax.persistence.MappedSuperclass")
 *              annotation("javax.persistence.Embeddable")
 *          }
 *
 */

@Service
class UserService(
    private val userRepository: UserRepository
) {

    @Transactional
    fun saveUser(request: UserCreateRequest) {
        val newUser = User(request.name, request.age)
        userRepository.save(newUser)
    }

    @Transactional(readOnly = true)
    fun getUsers(): List<UserResponse> {
        return userRepository.findAll()
            .map { user -> UserResponse.of(user) }
    }

    @Transactional
    fun updateUserName(request: UserUpdateRequest) {
        val user = userRepository.findByIdOrThrow(request.id)
        user.updateName(request.name)
    }

    @Transactional
    fun deleteUser(name: String?) {
        val user = userRepository.findByName(name!!) ?: fail()
        userRepository.delete(user)
    }

    @Transactional(readOnly = true)
    fun getUserLoanHistories(): List<UserLoanHistoryResponse> {
        return userRepository.findAllWithHistories()
            .map(UserLoanHistoryResponse::of)
    }
}