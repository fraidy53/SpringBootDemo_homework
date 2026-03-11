package com.example.myspringbootlab.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 비즈니스 로직 예외 클래스
 * RuntimeException을 상속받아 언체크 예외로 처리
 */
@Getter
public class BusinessException extends RuntimeException {

    // HTTP 상태 코드
    private final HttpStatus status;

    /**
     * @param message 예외 메시지
     * @param status HTTP 상태 코드
     */
    public BusinessException(String message, HttpStatus status) {
        super(message); // 여기서 RuntimeException의 생성자를 호출하여 메시지를 설정
        this.status = status;
    }

    /**
     * @param message 예외 메시지
     * @param status HTTP 상태 코드
     * @param cause 원인이 되는 예외
     */
    public BusinessException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}

