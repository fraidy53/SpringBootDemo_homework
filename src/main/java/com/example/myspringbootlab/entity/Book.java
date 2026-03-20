package com.example.myspringbootlab.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true, nullable = false)
    private String isbn;

    @Column(nullable = false)
    private LocalDate publishDate;

    @Column(nullable = false)
    private Integer price;

    // BookDetail과 1:1 양방향 연관관계 설정
    // @OneToOne의 mappedBy 속성은 BookDetail 엔티티의 book 필드를 참조한다
    // Book은 1:1 관계의 비주인(mappedBy).
    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    // ToString에서 bookDetail을 제외하여 순환 참조 방지
    @ToString.Exclude
    private BookDetail bookDetail;

    /**
     * 양방향 연관관계를 안전하게 동기화한다.
     * BookDetail이 null이 아니고, BookDetail의 book 필드가 현재 Book 인스턴스를 참조하지 않는 경우에만 setBookDetail을 호출한다.
     */
    public void setBookDetail(BookDetail bookDetail) {
        this.bookDetail = bookDetail;
        if (bookDetail != null && bookDetail.getBook() != this) {
            bookDetail.setBook(this);
        }
    }
}
