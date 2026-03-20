package com.example.myspringbootlab.Repository;

import com.example.myspringbootlab.entity.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn); // ISBN 단건 조회

    @Query("select b from Book b left join fetch b.bookDetail where b.id = :id")
    Optional<Book> findByIdWithBookDetail(@Param("id") Long id); // ID로 책 조회 시 BookDetail도 함께 조회하는 메서드 (Fetch Join 사용)

    @Query("select b from Book b left join fetch b.bookDetail where b.isbn = :isbn")
    Optional<Book> findByIsbnWithBookDetail(@Param("isbn") String isbn); // ISBN으로 책 조회 시 BookDetail도 함께 조회하는 메서드 (Fetch Join 사용)

    boolean existsByIsbn(String isbn); // ISBN이 존재하는지 여부를 확인하는 메서드

    List<Book> findByAuthorContainingIgnoreCase(String author); // 저자 이름에 특정 문자열이 포함된 책 검색 (대소문자 무시)

    List<Book> findByTitleContainingIgnoreCase(String title); // 제목 키워드 검색(대소문자 무시)
}
