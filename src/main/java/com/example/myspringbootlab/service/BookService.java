package com.example.myspringbootlab.service;

import com.example.myspringbootlab.Repository.BookRepository;
import com.example.myspringbootlab.dto.BookDTO;
import com.example.myspringbootlab.entity.Book;
import com.example.myspringbootlab.exception.BusinessException;
import com.example.myspringbootlab.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Book 비즈니스 로직을 담당하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    /**
     * 전체 도서를 상세 정보와 함께 조회한다.
     */
    public List<BookDTO.Response> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    /**
     * ID로 도서를 상세 정보와 함께 조회한다.
     */
    public BookDTO.Response getBookById(Long id) {
        Book book = bookRepository.findByIdWithBookDetail(id)
                .orElseThrow(() -> new BusinessException("ID: " + id + " 도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        return bookMapper.toResponse(book);
    }

    /**
     * ISBN으로 도서를 상세 정보와 함께 조회한다.
     */
    public BookDTO.Response getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbnWithBookDetail(isbn)
                .orElseThrow(() -> new BusinessException("ISBN: " + isbn + " 도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        return bookMapper.toResponse(book);
    }

    /**
     * 저자 키워드로 도서를 검색한다.
     */
    public List<BookDTO.Response> searchByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author)
                .stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    /**
     * 제목 키워드로 도서를 검색한다.
     */
    public List<BookDTO.Response> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    /**
     * 새 도서를 등록한다.
     */
    @Transactional
    public BookDTO.Response createBook(BookDTO.Request request) {
        validateIsbnDuplicate(request.getIsbn());
        Book savedBook = bookRepository.save(bookMapper.toEntity(request));
        return bookMapper.toResponse(savedBook);
    }

    /**
     * 기존 도서를 수정한다.
     */
    @Transactional
    public BookDTO.Response updateBook(Long id, BookDTO.Request request) {
        Book existing = bookRepository.findByIdWithBookDetail(id)
                .orElseThrow(() -> new BusinessException("ID: " + id + " 도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        boolean isbnChanged = !existing.getIsbn().equals(request.getIsbn());
        if (isbnChanged) {
            validateIsbnDuplicate(request.getIsbn());
        }

        bookMapper.updateEntity(existing, request);
        Book updated = bookRepository.save(existing);
        return bookMapper.toResponse(updated);
    }

    /**
     * 도서를 삭제한다.
     */
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException("ID: " + id + " 도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        bookRepository.delete(book);
    }
    // 공통 검증 : ISBN 중복 여부를 검증한다. 이미 존재하는 ISBN이면 BusinessException을 발생시킨다.
    private void validateIsbnDuplicate(String isbn) {
        if (bookRepository.existsByIsbn(isbn)) {
            throw new BusinessException("ISBN: " + isbn + " 는 이미 등록된 도서입니다.", HttpStatus.CONFLICT);
        }
    }
}
