package com.group.libraryapp.calculator

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

fun main() {
    val calculatorTest = CalculatorTest()
    calculatorTest.addTest()
    calculatorTest.minusTest()
    calculatorTest.multiplyTest()
    calculatorTest.divideTest()
    calculatorTest.divideTestByException()
}

class CalculatorTest {

    //    @Test
    fun addTest() {
        // Given
        val calculator = Calculator(5)

        // When
        calculator.add(3)

        // Then
        if (calculator.number != 8) {
            throw IllegalArgumentException()
        }

    }

    //    @Test
    fun minusTest() {
        // Given
        val calculator = Calculator(5)

        // When
        calculator.minus(3)

        // Then
        if (calculator.number != 2) {
            throw IllegalArgumentException()
        }

    }

    //    @Test
    fun multiplyTest() {
        // Given
        val calculator = Calculator(5)

        // When
        calculator.multiply(3)

        // Then
        if (calculator.number != 15) {
            throw IllegalArgumentException()
        }

    }

    //    @Test
    fun divideTest() {
        // Given
        val calculator = Calculator(5)

        // When
        calculator.divide(5)

        // Then
        if (calculator.number != 1) {
            throw IllegalArgumentException()
        }
    }

    //    @Test
    fun divideTestByException() {
        // Given
        val calculator = Calculator(5)

        // When
        try {
            calculator.divide(0)
        } catch (e: IllegalArgumentException) {
            if (e.message != "0으로 나눌 수 없습니다.") {
                throw IllegalArgumentException("메세지가 다릅니다.")
            }
            return
        } catch (e: Exception) {
            throw IllegalArgumentException()
        }
        throw IllegalArgumentException("기대하는 예외가 발생하지 않았습니다.")
    }
}