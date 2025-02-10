package org.example.redistest.common

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

private val logger = KotlinLogging.logger {}

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<String> {

        //val traceId = MDC.get("traceId") ?: "N/A"
        logger.error(ex) { "Error occurred during request processing" }
        // 추가적인 DEBUG 레벨의 상세 로그를 기록할 수 있습니다.
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("An error occurred.")
    }
}