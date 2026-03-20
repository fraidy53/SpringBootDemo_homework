package com.example.myspringbootlab.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 책의 상세 정보를 저장하는 엔티티.
 * BookDetail이 1:1 관계의 주인이며 외래키(book_id)를 소유한다.
 */
@Entity
@Table(name = "book_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BookDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String description;

    private String language;

    private Integer pageCount;

    private String publisher;

    private String coverImageUrl;

    private String edition;

    // 상세에서 Book은 필요할 때 로딩하도록 LAZY로 설정
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", unique = true) // BookDetail이 1:1 관계의 주인으로 외래키(book_id)를 소유한다
    @ToString.Exclude
    private Book book;

    /**
     * 양방향 연관관계를 안전하게 동기화한다.
     * Book이 null이 아니고, Book의 bookDetail 필드가 현재 BookDetail 인스턴스를 참조하지 않는 경우에만 setBook을 호출한다.
     * BookDetail이 Book을 설정할 때, Book도 BookDetail을 설정하여 양방향 연관관계를 유지한다.
     */
    public void setBook(Book book) {
        this.book = book;
        if (book != null && book.getBookDetail() != this) {
            book.setBookDetail(this);
        }
    }
}

