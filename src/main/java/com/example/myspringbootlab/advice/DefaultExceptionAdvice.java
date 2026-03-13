package com.example.myspringbootlab.advice;

import com.example.myspringbootlab.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 전역 예외 처리 클래스
 * @RestControllerAdvice: 모든 @RestController에서 발생하는 예외를 처리
 */
@RestControllerAdvice // @ControllerAdvice + @ResponseBody
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
     * @Valid 검증 실패를 처리한다.
     *
     * @param ex MethodArgumentNotValidException
     * @param request HttpServletRequest
     * @return ResponseEntity<ErrorObject> - 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorObject> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String validationMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));

        ErrorObject errorObject = ErrorObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(validationMessage)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    /**
     * DB 제약조건(중복키/NOT NULL 등) 위반을 처리한다.
     *
     * @param ex DataIntegrityViolationException
     * @param request HttpServletRequest
     * @return ResponseEntity<ErrorObject> - 409 Conflict
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorObject> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        ErrorObject errorObject = ErrorObject.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .message("데이터 무결성 오류입니다. 중복 값 또는 제약조건을 확인하세요.")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorObject, HttpStatus.CONFLICT);
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

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
