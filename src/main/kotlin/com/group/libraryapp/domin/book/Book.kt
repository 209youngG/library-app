package com.group.libraryapp.domin.book

import javax.persistence.*

@Entity
class Book constructor(
    val name: String,
    /**
     * 하기 어노테이션 추가 하지 않으면 Enum타입의 순서가 DB에 들어가게 되므로
     * 해당 순서가 변경 될 시 초기 데이터와 맞지 않게 되는 문제 발생하지 않게 선언을 추천
      */
    @Enumerated(EnumType.STRING)
    val type: BookType,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    init {
        when {
            name.isBlank() -> {
                throw IllegalArgumentException("이름은 비어 있을 수 없습니다")
            }
        }
    }

    companion object {
        fun fixture(
            name: String = "책 이름",
            type: BookType = BookType.COMPUTER,
            id: Long? = null,
        ): Book {
            return Book(
                name = name,
                type = type,
                id = id,
            )
        }
    }
}