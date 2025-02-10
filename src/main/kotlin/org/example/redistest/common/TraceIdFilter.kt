/*
package org.example.redistest.common

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import jakarta.servlet.Filter
import jakarta.servlet.ServletRequest
import jakarta.servlet.FilterChain
import jakarta.servlet.FilterConfig
import jakarta.servlet.ServletException
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import java.io.IOException
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class TraceIdFilter : Filter {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val traceId = UUID.randomUUID().toString()
       // MDC.put("traceId", traceId)
        MDC.put("collectAllLogs", "true")
        // 오류 처리 로직
        try {
            // 요청 URI와 HTTP 메서드를 로그에 추가
            val requestUri = httpRequest.requestURI
            val httpMethod = httpRequest.method
            //logger.info { "Incoming request $httpMethod $requestUri with Trace ID: $traceId" }

            chain.doFilter(request, response)
        } finally {
            //MDC.remove("traceId")
            MDC.remove("collectAllLogs")
        }
    }

    override fun init(filterConfig: FilterConfig) {}
    override fun destroy() {}
}*/
