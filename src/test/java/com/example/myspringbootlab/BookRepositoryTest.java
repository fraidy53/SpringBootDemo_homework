package com.example.myspringbootlab;

import com.example.myspringbootlab.Repository.BookDetailRepository;
import com.example.myspringbootlab.Repository.BookRepository;
import com.example.myspringbootlab.entity.Book;
import com.example.myspringbootlab.entity.BookDetail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Book/BookDetail 레포지토리 테스트.
 * @DataJpaTest는 JPA 관련 컴포넌트만 로드하여 빠르게 영속성 계층을 검증한다.
 */
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookDetailRepository bookDetailRepository;

    @Test
    public void createBookWithBookDetail() { // 책과 책 상세 정보를 함께 저장하는 테스트
        // 1:1 저장 + cascade 확인, cascade : Book 엔티티가 저장될 때 BookDetail도 함께 저장되는지 검증
        // Given
        Book book = Book.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .price(45)
                .publishDate(LocalDate.of(2008, 8, 1))
                .build();

        BookDetail bookDetail = BookDetail.builder()
                .description("A handbook of agile software craftsmanship")
                .language("English")
                .pageCount(464)
                .publisher("Prentice Hall")
                .coverImageUrl("https://example.com/cleancode.jpg")
                .edition("1st")
                .book(book)
                .build();

        book.setBookDetail(bookDetail);

        // When
        Book savedBook = bookRepository.save(book);

        // Then
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Clean Code");
        assertThat(savedBook.getIsbn()).isEqualTo("9780132350884");
        assertThat(savedBook.getBookDetail()).isNotNull();
        assertThat(savedBook.getBookDetail().getPublisher()).isEqualTo("Prentice Hall");
        assertThat(savedBook.getBookDetail().getPageCount()).isEqualTo(464);
    }

    @Test
    public void findBookByIsbn() { // isbn으로 책을 조회하는 테스트
        // Given
        Book book = Book.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .price(45)
                .publishDate(LocalDate.of(2008, 8, 1))
                .build();

        BookDetail bookDetail = BookDetail.builder()
                .description("A handbook of agile software craftsmanship")
                .language("English")
                .pageCount(464)
                .publisher("Prentice Hall")
                .coverImageUrl("https://example.com/cleancode.jpg")
                .edition("1st")
                .book(book)
                .build();

        book.setBookDetail(bookDetail);
        bookRepository.save(book);

        // When
        Optional<Book> foundBook = bookRepository.findByIsbn("9780132350884");

        // Then
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Clean Code");
    }

    @Test
    public void findByIdWithBookDetail() { // fetch join을 사용하여 ID로 책과 책 상세 정보를 함께 조회하는 테스트
        // Given
        Book book = Book.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .price(45)
                .publishDate(LocalDate.of(2008, 8, 1))
                .build();

        BookDetail bookDetail = BookDetail.builder()
                .description("A handbook of agile software craftsmanship")
                .language("English")
                .pageCount(464)
                .publisher("Prentice Hall")
                .coverImageUrl("https://example.com/cleancode.jpg")
                .edition("1st")
                .book(book)
                .build();

        book.setBookDetail(bookDetail);
        Book savedBook = bookRepository.save(book);

        // When
        Optional<Book> foundBook = bookRepository.findByIdWithBookDetail(savedBook.getId());
        // findByIdWithBookDetail 메서드는 fetch join을 사용하여 Book과 BookDetail을 한 번의 쿼리로 조회한다.
        // 따라서 N+1 문제 없이 책과 책 상세 정보를 함께 가져올 수 있다.

        // Then
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getBookDetail()).isNotNull();
        assertThat(foundBook.get().getBookDetail().getPublisher()).isEqualTo("Prentice Hall");
    }

    @Test
    public void findBooksByAuthor() { // 저자 이름에 특정 문자열이 포함된 책을 검색하는 테스트 (대소문자 무시)
        // Given
        Book book1 = Book.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .price(45)
                .publishDate(LocalDate.of(2008, 8, 1))
                .build();

        Book book2 = Book.builder()
                .title("Clean Architecture")
                .author("Robert C. Martin")
                .isbn("9780134494166")
                .price(50)
                .publishDate(LocalDate.of(2017, 9, 20))
                .build();

        Book book3 = Book.builder()
                .title("Effective Java")
                .author("Joshua Bloch")
                .isbn("9780134685991")
                .price(55)
                .publishDate(LocalDate.of(2018, 1, 6))
                .build();

        bookRepository.saveAll(List.of(book1, book2, book3));

        // When
        List<Book> martinBooks = bookRepository.findByAuthorContainingIgnoreCase("martin");

        // Then
        assertThat(martinBooks).hasSize(2);
        assertThat(martinBooks).extracting(Book::getTitle)
                .containsExactlyInAnyOrder("Clean Code", "Clean Architecture");
    }

    @Test
    public void findBookDetailByBookId() { // bookId로 책 상세 정보를 조회하는 테스트. 상세 레포지토리 조회 확인
        // Given
        Book book = Book.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .price(45)
                .publishDate(LocalDate.of(2008, 8, 1))
                .build();

        BookDetail bookDetail = BookDetail.builder()
                .description("A handbook of agile software craftsmanship")
                .language("English")
                .pageCount(464)
                .publisher("Prentice Hall")
                .coverImageUrl("https://example.com/cleancode.jpg")
                .edition("1st")
                .book(book)
                .build();

        book.setBookDetail(bookDetail);
        Book savedBook = bookRepository.save(book);

        // When
        Optional<BookDetail> foundBookDetail = bookDetailRepository.findByBookId(savedBook.getId());

        // Then
        assertThat(foundBookDetail).isPresent();
        assertThat(foundBookDetail.get().getDescription()).contains("agile software craftsmanship");
    }
}
