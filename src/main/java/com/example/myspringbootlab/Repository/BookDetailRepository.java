package com.example.myspringbootlab.Repository;

import com.example.myspringbootlab.entity.BookDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookDetailRepository extends JpaRepository<BookDetail, Long> { //

    Optional<BookDetail> findByBookId(Long bookId); // 책 ID로 BookDetail 조회

    @Query("select bd from BookDetail bd join fetch bd.book where bd.id = :id")
    Optional<BookDetail> findByIdWithBook(@Param("id") Long id); // BookDetail ID로 조회 시 Book 정보도 함께 조회하는 메서드 (Fetch Join 사용)

    List<BookDetail> findByPublisher(String publisher); // 출판사 기준 상세 목록 조회
}

