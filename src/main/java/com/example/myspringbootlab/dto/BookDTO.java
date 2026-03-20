package com.example.myspringbootlab.dto;

import com.example.myspringbootlab.entity.Book;
import com.example.myspringbootlab.entity.BookDetail;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Book API 요청/응답 DTO 묶음 클래스.
 */
public class BookDTO {

    /**
     * 생성/수정 요청 DTO.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        @NotBlank(message = "Book title is required")
        private String title;

        @NotBlank(message = "Author name is required")
        private String author;

        @NotBlank(message = "ISBN is required")
        @Pattern(
                // regexp 설명: ISBN-10 또는 ISBN-13 형식 허용 (하이픈 포함 가능)
                regexp = "^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$",
                message = "ISBN must be valid (10 or 13 digits, with or without hyphens)"
        )
        private String isbn;

        @PositiveOrZero(message = "Price must be positive or zero")
        private Integer price;

        @Past(message = "Publish date must be in the past")
        private LocalDate publishDate;

        @Valid // BookDetailDTO의 유효성 검사 활성화, 중첩 상세 검증 가능
        private BookDetailDTO detailRequest;
    }

    /**
     * 상세 정보 요청 DTO.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookDetailDTO {
        private String description;
        private String language;
        private Integer pageCount;
        private String publisher;
        private String coverImageUrl;
        private String edition;
    }

    /**
     * 응답 DTO.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response { // API 응답에 필요한 필드와 BookDetailResponse를 포함하는 DTO
        private Long id;
        private String title;
        private String author;
        private String isbn;
        private Integer price;
        private LocalDate publishDate;
        private BookDetailResponse detail;

        public static Response fromEntity(Book book) { // Book 엔티티를 Response DTO로 변환하는 정적 팩토리 메서드
            BookDetail detail = book.getBookDetail();
            BookDetailResponse detailResponse = detail != null
                    ? BookDetailResponse.builder()
                    .id(detail.getId())
                    .description(detail.getDescription())
                    .language(detail.getLanguage())
                    .pageCount(detail.getPageCount())
                    .publisher(detail.getPublisher())
                    .coverImageUrl(detail.getCoverImageUrl())
                    .edition(detail.getEdition())
                    .build()
                    : null;

            return Response.builder()
                    .id(book.getId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .isbn(book.getIsbn())
                    .price(book.getPrice())
                    .publishDate(book.getPublishDate())
                    .detail(detailResponse)
                    .build();
        }
    }

    /**
     * 상세 정보 응답 DTO.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookDetailResponse { // 상세 응답 데이터
        private Long id;
        private String description;
        private String language;
        private Integer pageCount;
        private String publisher;
        private String coverImageUrl;
        private String edition;
    }
}
