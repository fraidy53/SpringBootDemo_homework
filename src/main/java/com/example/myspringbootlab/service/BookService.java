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
     * 도서를 생성한다.
     */
    @Transactional
    public BookDTO.BookResponse createBook(BookDTO.BookCreateRequest request) {
        Book savedBook = bookRepository.save(bookMapper.toEntity(request));
        return bookMapper.toResponse(savedBook);
    }

    /**
     * 전체 도서 목록을 조회한다.
     */
    public List<BookDTO.BookResponse> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    /**
     * ID로 도서를 조회한다. 없으면 404 예외를 던진다.
     */
    public BookDTO.BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException("ID: " + id + " 도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        return bookMapper.toResponse(book);
    }

    /**
     * ISBN으로 도서를 조회한다. 없으면 404 예외를 던진다.
     */
    public BookDTO.BookResponse getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BusinessException("ISBN: " + isbn + " 도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        return bookMapper.toResponse(book);
    }

    /**
     * 도서 정보를 부분 수정한다. null이 아닌 값만 반영한다.
     */
    @Transactional
    public BookDTO.BookResponse updateBook(Long id, BookDTO.BookUpdateRequest request) {
        Book existBook = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException("ID: " + id + " 도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        bookMapper.applyNonNullUpdates(existBook, request);
        Book updatedBook = bookRepository.save(existBook);

        return bookMapper.toResponse(updatedBook);
    }

    /**
     * 도서를 삭제한다. 없으면 404 예외를 던진다.
     */
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException("ID: " + id + " 도서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        bookRepository.delete(book);
    }
}

