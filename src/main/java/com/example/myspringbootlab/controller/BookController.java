package com.example.myspringbootlab.controller;

import com.example.myspringbootlab.dto.BookDTO;
import com.example.myspringbootlab.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Book API 컨트롤러.
 * HTTP 요청/응답을 처리하고, 실제 비즈니스 로직은 서비스 계층에 위임한다.
 * 각 메서드는 RESTful한 엔드포인트를 제공하며, 요청 본문 검증(@Valid)과 예외 처리는 전역 예외 처리기로 일괄 관리한다.
 * 엔드포인트 :
 * - GET /api/books : 전체 도서 조회
 * - GET /api/books/{id} : ID 기반 단건 조회
 * - GET /api/books/isbn/{isbn} : ISBN 기반 단건 조회
 * - GET /api/books/search/author?author=키워드 : 저자 키워드 검색
 * - GET /api/books/search/title?title=키워드 : 제목 키워드 검색
 * - POST /api/books : 도서 생성
 * - PUT /api/books/{id} : 도서 수정
 * - DELETE /api/books/{id} : 도서 삭제
 * 생성은 201 Created, 수정과 삭제는 200 OK를 반환한다. 요청 본문 검증 실패 시 400 Bad Request를 반환한다.
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * 전체 도서 조회.
     */
    @GetMapping
    public ResponseEntity<List<BookDTO.Response>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    /**
     * ID 기반 단건 조회.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO.Response> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    /**
     * ISBN 기반 단건 조회.
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDTO.Response> getBookByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.getBookByIsbn(isbn));
    }

    /**
     * 저자 키워드 검색.
     * 예: /api/books/search/author?author=martin
     */
    @GetMapping("/search/author")
    public ResponseEntity<List<BookDTO.Response>> searchByAuthor(@RequestParam String author) {
        return ResponseEntity.ok(bookService.searchByAuthor(author));
    }

    /**
     * 제목 키워드 검색.
     * 예: /api/books/search/title?title=clean
     */
    @GetMapping("/search/title")
    public ResponseEntity<List<BookDTO.Response>> searchByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.searchByTitle(title));
    }

    /**
     * 도서 생성.
     * 요청 본문 검증(@Valid)이 실패하면 전역 예외 처리기로 400 응답을 반환한다.
     */
    @PostMapping
    public ResponseEntity<BookDTO.Response> createBook(@Valid @RequestBody BookDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(request));
    }

    /**
     * 도서 수정.
     * ID로 기존 도서를 찾고, 요청 값으로 갱신한 뒤 수정 결과를 반환한다.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookDTO.Response> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookDTO.Request request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    /**
     * 도서 삭제.
     * Book이 삭제되면 cascade 설정으로 BookDetail도 함께 삭제된다.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Id = " + id + " 도서가 삭제되었습니다.");
    }
}
