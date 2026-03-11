package com.example.myspringbootlab.advice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 에러 응답 객체
 * 클라이언트에게 에러 정보를 JSON 형태로 반환
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorObject {

    // 에러 발생 시간
    private LocalDateTime timestamp;

    // HTTP 상태 코드
    private Integer status;

    // 에러 메시지
    private String message;

    // 요청 경로
    private String path;
}

