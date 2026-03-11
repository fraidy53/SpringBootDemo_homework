package com.example.myspringbootlab;

import com.example.myspringbootlab.Repository.BookRepository;
import com.example.myspringbootlab.entity.Book;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * BookRepository 통합 테스트 클래스
 *
 * @SpringBootTest(webEnvironment = NONE): 웹 서버를 시작하지 않고 테스트 실행 (8080 포트 충돌 방지)
 * @ActiveProfiles("prod"): prod 프로파일 활성화 (MariaDB lab_db 사용)
 * @Transactional: 각 테스트 메서드를 트랜잭션으로 감싸서 실행 후 자동 롤백
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("prod")
@Transactional
public class BookRepositoryTest {

    // Spring 컨테이너에서 BookRepository 빈을 자동 주입
    @Autowired
    BookRepository bookRepository;

    /**
     * 도서 등록 테스트 (CREATE)
     *
     * @Test: JUnit 테스트 메서드임을 표시
     * @Rollback(false): 테스트 후 데이터를 롤백하지 않고 DB에 실제로 저장
     * @Disabled: 이 테스트를 비활성화 (다른 테스트와 충돌 방지)
     *
     * 동작: Book 객체 2개를 생성하여 DB에 저장
     */
    @Test
    @Rollback(value = false)
    @Disabled
    void testCreateBook(){
        // 첫 번째 도서 객체 생성 및 속성 설정
        Book book1 = new Book();
        book1.setTitle("스프링 부트 입문");
        book1.setAuthor("홍길동");
        book1.setIsbn("9788956746425");
        book1.setPrice(30000);
        book1.setPublishDate(LocalDate.of(2025,5,7));

        // 두 번째 도서 객체 생성 및 속성 설정
        Book book2 = new Book();
        book2.setTitle("JPA 프로그래밍");
        book2.setAuthor("박둘리");
        book2.setIsbn("9788956746432");
        book2.setPrice(35000);
        book2.setPublishDate(LocalDate.of(2025,4,30));

        // JpaRepository의 save() 메서드로 DB에 저장
        // ID가 없으면 INSERT, 있으면 UPDATE 수행
        bookRepository.save(book1);
        bookRepository.save(book2);
    }

    /**
     * ISBN으로 도서 조회 테스트 (READ)
     *
     * 동작: ISBN 번호로 도서를 조회하여 존재하면 출력
     * Optional<Book>: 조회 결과가 없을 수 있으므로 Optional로 감싸서 반환
     */
    @Test
    void testFindByIsbn() {
        // ISBN으로 도서 조회 (Spring Data JPA의 쿼리 메서드)
        Optional<Book> book = bookRepository.findByIsbn("9788956746425");

        // Optional이 값을 포함하고 있으면 람다식 실행 (도서 정보 출력)
        // ifPresent(): NullPointerException 방지
        book.ifPresent(System.out::println);
    }

    /**
     * 저자명으로 도서 목록 조회 테스트 (READ)
     *
     * 동작: 저자명으로 도서 목록을 조회하여 모두 출력
     * List<Book>: 같은 저자의 도서가 여러 권일 수 있으므로 List로 반환
     */
    @Test
    void testFindByAuthor() {
        // 저자명으로 도서 목록 조회 (Spring Data JPA의 쿼리 메서드)
        List<Book> books = bookRepository.findByAuthor("홍길동");

        // forEach(): 리스트의 각 요소를 순회하며 출력
        // 메서드 참조(::) 사용
        books.forEach(System.out::println);
    }

    /**
     * 도서 정보 수정 테스트 (UPDATE)
     *
     * 동작: ISBN으로 도서를 조회한 후, 가격을 수정하여 저장
     * JPA의 변경 감지(Dirty Checking) 또는 save() 메서드로 업데이트
     */
    @Test
    void testUpdateBook() {
        // ISBN으로 도서 조회
        Optional<Book> optionalBook = bookRepository.findByIsbn("9788956746425");

        // 도서가 존재하는지 확인
        if(optionalBook.isPresent()){
            // Optional에서 실제 Book 객체 꺼내기
            Book book = optionalBook.get();

            // 가격 수정 (30000원 → 32000원)
            book.setPrice(32000);

            // 수정된 엔티티 저장 (UPDATE 쿼리 실행)
            // JPA가 ID가 있는 엔티티는 UPDATE로 처리
            bookRepository.save(book);
        }
    }

    /**
     * 도서 삭제 테스트 (DELETE)
     *
     * 동작: ISBN으로 도서를 조회한 후 삭제
     */
    @Test
    void testDeleteBook() {
        // ISBN으로 도서 조회
        Optional<Book> optionalBook = bookRepository.findByIsbn("9788956746425");

        // 도서가 존재하면 삭제 (람다식 사용)
        // ifPresent(): Optional에 값이 있을 때만 실행
        // delete(): JpaRepository의 삭제 메서드 (DELETE 쿼리 실행)
        optionalBook.ifPresent(book -> bookRepository.delete(book));
    }
}
