package com.group.libraryapp

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JunitTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            println("전체 테스트 시작 전")
        }
        @AfterAll
        @JvmStatic
        fun afterAll() {
            println("전체 테스트 종료 후")
        }
    }

    @BeforeEach
    fun setUp() {
        println("각 테스트 시작 전")
    }

    @AfterEach
    fun tearDown() {
        println("각 테스트 종료 후")
    }

    @Test
    fun test1() {
        println("테스트1")
    }

    @Test
    fun test2() {
        println("테스트2")
    }
}