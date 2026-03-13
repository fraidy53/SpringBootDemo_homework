package com.example.myspringbootlab.mapper;

import com.example.myspringbootlab.dto.BookDTO;
import com.example.myspringbootlab.entity.Book;
import org.springframework.stereotype.Component;

/**
 * Book 엔티티와 DTO 간 변환을 담당하는 매퍼
 */
@Component
public class BookMapper {

    /**
     * 생성 요청 DTO를 엔티티로 변환한다.
     */
    public Book toEntity(BookDTO.BookCreateRequest request) {
        return Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .price(request.getPrice())
                .publishDate(request.getPublishDate())
                .build();
    }

    /**
     * 엔티티를 응답 DTO로 변환한다.
     */
    public BookDTO.BookResponse toResponse(Book entity) {
        return BookDTO.BookResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .isbn(entity.getIsbn())
                .price(entity.getPrice())
                .publishDate(entity.getPublishDate())
                .build();
    }

    /**
     * 수정 요청 DTO에서 null이 아닌 필드만 엔티티에 반영한다.
     */
    public void applyNonNullUpdates(Book entity, BookDTO.BookUpdateRequest request) {
        if (request.getTitle() != null) {
            entity.setTitle(request.getTitle());
        }
        if (request.getAuthor() != null) {
            entity.setAuthor(request.getAuthor());
        }
        if (request.getPrice() != null) {
            entity.setPrice(request.getPrice());
        }
        if (request.getPublishDate() != null) {
            entity.setPublishDate(request.getPublishDate());
        }
    }
}

