package com.example.myspringbootlab.controller;

import com.example.myspringbootlab.Repository.BookRepository;
import com.example.myspringbootlab.entity.Book;
import com.example.myspringbootlab.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Book REST API 컨트롤러
 *
 * @RestController: @Controller + @ResponseBody
 *                  모든 메서드의 반환값이 JSON/XML로 직렬화됨
 * @RequestMapping: 기본 경로를 /api/books로 설정
 * + REST API 만드는 이유 : 프론트앤드/앱/다른 서버 등 누구든 데이터를 주고받을 수 있는 창구 만들려고
 */
@RestController
@RequestMapping("/api/books")
public class BookRestController {

    @Autowired
    private BookRepository bookRepository;

    /**
     * 1. 새 도서 등록 (CREATE)
     * POST /api/books
     *
     * @param book 요청 본문(JSON)에서 자동으로 Book 객체로 변환
     * @return ResponseEntity<Book> - 생성된 도서 정보 + 201 Created
     *
     * 예시 요청:
     * POST /api/books
     * Content-Type: application/json
     * {
     *   "title": "스프링 부트 입문",
     *   "author": "홍길동",
     *   "isbn": "9788956746425",
     *   "price": 30000,
     *   "publishDate": "2025-05-07"
     * }
     */
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        // 도서 저장 (JPA가 자동으로 ID 생성)
        Book savedBook = bookRepository.save(book);

        // 201 Created 상태 코드와 함께 저장된 도서 반환
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    /**
     * 2. 모든 도서 조회 (READ ALL)
     * GET /api/books
     *
     * @return List<Book> - 모든 도서 목록 + 200 OK
     *
     * 예시 요청:
     * GET /api/books
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        // JpaRepository의 findAll() 메서드로 전체 조회
        List<Book> books = bookRepository.findAll();

        // 200 OK 상태 코드와 함께 도서 목록 반환
        return ResponseEntity.ok(books);
    }

    /**
     * 3. ID로 특정 도서 조회 (READ ONE)
     * GET /api/books/{id}
     *
     * @param id 경로 변수에서 추출한 도서 ID
     * @return ResponseEntity<Book> - 조회된 도서 정보 + 200 OK
     *                                또는 404 Not Found
     *
     * 예시 요청:
     * GET /api/books/1
     *
     * Optional의 map() / orElse() 사용 방식
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) { // URL 경로의 값을 변수로 받는 어노테이션
        // Optional<Book>을 ResponseEntity로 변환
        return bookRepository.findById(id)
                // Optional에 값이 있으면: ResponseEntity.ok(book) 반환
                .map(ResponseEntity::ok)
                // Optional이 비어있으면: 404 Not Found 반환
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 4. ISBN으로 도서 조회 (READ ONE)
     * GET /api/books/isbn/{isbn}
     *
     * @param isbn 경로 변수에서 추출한 ISBN
     * @return Book - 조회된 도서 정보 + 200 OK
     *                또는 BusinessException 발생 → 404 Not Found
     *
     * 예시 요청:
     * GET /api/books/isbn/9788956746425
     *
     * BusinessException 사용 방식 (DefaultExceptionAdvice에서 처리)
     */
    @GetMapping("/isbn/{isbn}")
    public Book getBookByIsbn(@PathVariable String isbn) {
        // Optional에서 값을 꺼내거나, 없으면 BusinessException 발생
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BusinessException(
                        "ISBN: " + isbn + "에 해당하는 도서를 찾을 수 없습니다.",
                        HttpStatus.NOT_FOUND
                ));
    }

    /**
     * 5. 도서 정보 수정 (UPDATE)
     * PUT /api/books/{id}
     *
     * @param id 수정할 도서의 ID
     * @param bookDetails 수정할 도서 정보
     * @return ResponseEntity<Book> - 수정된 도서 정보 + 200 OK
     *                                또는 404 Not Found
     *
     * 예시 요청:
     * PUT /api/books/1
     * Content-Type: application/json
     * {
     *   "title": "스프링 부트 심화",
     *   "author": "홍길동",
     *   "isbn": "9788956746425",
     *   "price": 35000,
     *   "publishDate": "2025-05-07"
     * }
     */
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @PathVariable Long id,
            @RequestBody Book bookDetails) {

        // ID로 도서 조회
        return bookRepository.findById(id)
                .map(book -> {
                    // 기존 도서 정보를 새 정보로 업데이트
                    book.setTitle(bookDetails.getTitle());
                    book.setAuthor(bookDetails.getAuthor());
                    book.setIsbn(bookDetails.getIsbn());
                    book.setPrice(bookDetails.getPrice());
                    book.setPublishDate(bookDetails.getPublishDate());

                    // 수정된 도서 저장 (JPA가 UPDATE 쿼리 실행)
                    Book updatedBook = bookRepository.save(book);

                    // 200 OK와 함께 수정된 도서 반환
                    return ResponseEntity.ok(updatedBook);
                })
                // 도서가 없으면 404 Not Found
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 6. 도서 삭제 (DELETE)
     * DELETE /api/books/{id}
     *
     * @param id 삭제할 도서의 ID
     * @return ResponseEntity<Void> - 204 No Content (성공)
     *                                또는 404 Not Found
     *
     * 예시 요청:
     * DELETE /api/books/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        // ID로 도서 조회
        return bookRepository.findById(id)
                .map(book -> {
                    // 도서 삭제 (DELETE 쿼리 실행)
                    bookRepository.delete(book);

                    // 204 No Content 반환 (삭제 성공, 반환할 내용 없음)
                    return ResponseEntity.ok("Id = " + id + " 도서가 삭제되었습니다.");
                })
                // 도서가 없으면 404 Not Found
                .orElse(ResponseEntity.notFound().build());
    }
}

