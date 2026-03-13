package com.example.myspringbootlab.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * API 요청/응답에서 사용하는 Book DTO 모음 클래스
 */
public class BookDTO {

    /**
     * 도서 생성 요청 DTO
     * 모든 필드는 필수값으로 검증한다.
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookCreateRequest {

        @NotBlank(message = "제목은 필수입니다.")
        private String title;

        @NotBlank(message = "저자는 필수입니다.")
        private String author;

        @NotBlank(message = "ISBN은 필수입니다.")
        @Pattern(regexp = "^[0-9]{13}$", message = "ISBN은 13자리 숫자여야 합니다.")
        private String isbn;

        @NotNull(message = "가격은 필수입니다.")
        @Min(value = 1, message = "가격은 1원 이상이어야 합니다.")
        private Integer price;

        @NotNull(message = "출판일은 필수입니다.")
        private LocalDate publishDate;
    }

    /**
     * 도서 수정 요청 DTO
     * null이 아닌 필드만 부분 업데이트에 반영한다.
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookUpdateRequest {

        private String title;

        private String author;

        @Min(value = 1, message = "가격은 1원 이상이어야 합니다.")
        private Integer price;

        private LocalDate publishDate;
    }

    /**
     * 도서 응답 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookResponse {

        private Long id;
        private String title;
        private String author;
        private String isbn;
        private Integer price;
        private LocalDate publishDate;
    }
}

