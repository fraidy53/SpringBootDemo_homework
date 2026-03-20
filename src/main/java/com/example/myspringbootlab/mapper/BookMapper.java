package com.example.myspringbootlab.mapper;

import com.example.myspringbootlab.dto.BookDTO;
import com.example.myspringbootlab.entity.Book;
import com.example.myspringbootlab.entity.BookDetail;
import org.springframework.stereotype.Component;

/**
 * Book 엔티티와 DTO 간 변환을 담당하는 매퍼
 */
@Component
public class BookMapper {

    /**
     * 생성/수정 요청 DTO를 엔티티로 변환한다.
     * Book 생성 + detailRequest 있으면 BookDetail 생성 후 book.setBookDetail(detail)로 관계 연결
     */
    public Book toEntity(BookDTO.Request request) {
        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .price(request.getPrice())
                .publishDate(request.getPublishDate())
                .build();

        if (request.getDetailRequest() != null) {
            BookDetail detail = BookDetail.builder()
                    .description(request.getDetailRequest().getDescription())
                    .language(request.getDetailRequest().getLanguage())
                    .pageCount(request.getDetailRequest().getPageCount())
                    .publisher(request.getDetailRequest().getPublisher())
                    .coverImageUrl(request.getDetailRequest().getCoverImageUrl())
                    .edition(request.getDetailRequest().getEdition())
                    .build();
            book.setBookDetail(detail);
        }

        return book;
    }

    /**
     * 엔티티를 응답 DTO로 변환한다.
     */
    public BookDTO.Response toResponse(Book entity) {
        return BookDTO.Response.fromEntity(entity);
    }

    /**
     * 요청 DTO를 기준으로 엔티티를 업데이트한다.
     */
    public void updateEntity(Book book, BookDTO.Request request) {
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPrice(request.getPrice());
        book.setPublishDate(request.getPublishDate());

        if (request.getDetailRequest() == null) { // 상세가 없을 때
            book.setBookDetail(null);
            return;
        }

        BookDetail detail = book.getBookDetail(); // 기존 상세 조회
        if (detail == null) { // 기존에 상세가 없었는데, 새로 추가하는 경우
            detail = new BookDetail();
            book.setBookDetail(detail); // book.setBookDetail(detail)로 양방향 연관관계 동기화
        }
        // 상세가 있을 때, 기존 상세 업데이트
        detail.setDescription(request.getDetailRequest().getDescription());
        detail.setLanguage(request.getDetailRequest().getLanguage());
        detail.setPageCount(request.getDetailRequest().getPageCount());
        detail.setPublisher(request.getDetailRequest().getPublisher());
        detail.setCoverImageUrl(request.getDetailRequest().getCoverImageUrl());
        detail.setEdition(request.getDetailRequest().getEdition());
    }
}
