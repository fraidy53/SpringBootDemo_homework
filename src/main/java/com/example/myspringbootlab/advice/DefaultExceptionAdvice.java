package com.example.myspringbootlab.advice;

import com.example.myspringbootlab.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * 전역 예외 처리 클래스
 * @RestControllerAdvice: 모든 @RestController에서 발생하는 예외를 처리
 */
@RestControllerAdvice
public class DefaultExceptionAdvice {

    /**
     * BusinessException 처리
     * 비즈니스 로직에서 발생하는 예외를 처리하여 ErrorObject로 변환
     *
     * @param ex BusinessException
     * @param request HttpServletRequest
     * @return ResponseEntity<ErrorObject> - 에러 정보와 HTTP 상태 코드
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorObject> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {

        // ErrorObject 생성 (빌더 패턴 사용)
        ErrorObject errorObject = ErrorObject.builder()
                .timestamp(LocalDateTime.now())           // 현재 시간
                .status(ex.getStatus().value())          // HTTP 상태 코드 (404, 400 등)
                .message(ex.getMessage())                // 예외 메시지
                .path(request.getRequestURI())           // 요청 경로 (/api/books/1 등)
                .build(); // 빌더 패턴으로 객체 생성, 설계 다 했으니 이제 진짜 객체 만들기

        // ResponseEntity로 ErrorObject와 상태 코드 반환
        return new ResponseEntity<>(errorObject, ex.getStatus());
    }

    /**
     * 일반 예외 처리 (예상하지 못한 에러)
     *
     * @param ex Exception
     * @param request HttpServletRequest
     * @return ResponseEntity<ErrorObject> - 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorObject> handleGeneralException(
            Exception ex,
            HttpServletRequest request) {

        ErrorObject errorObject = ErrorObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("서버 내부 오류가 발생했습니다: " + ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

