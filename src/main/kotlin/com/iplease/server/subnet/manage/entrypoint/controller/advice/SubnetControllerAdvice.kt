package com.iplease.server.subnet.manage.entrypoint.controller.advice

import com.iplease.server.subnet.manage.data.response.ErrorResponse
import com.iplease.server.subnet.manage.data.type.ErrorCode
import com.iplease.server.subnet.manage.entrypoint.controller.SubnetController
import com.iplease.server.subnet.manage.exception.DuplicateSubnetException
import com.iplease.server.subnet.manage.exception.MalformedSubnetException
import com.iplease.server.subnet.manage.exception.PermissionDeniedException
import com.iplease.server.subnet.manage.exception.UnknownSubnetException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@RestControllerAdvice(basePackageClasses = [SubnetController::class])
class SubnetControllerAdvice {
    //TODO 나중에 AOP 도입 고민해보기
    val LOGGER = LoggerFactory.getLogger(SubnetControllerAdvice::class.java)

    @ExceptionHandler(DuplicateSubnetException::class)
    fun handle(e: DuplicateSubnetException): Mono<ResponseEntity<ErrorResponse>> {
        LOGGER.warn("error occurred during handle request!")
        LOGGER.warn("- error : ${e.javaClass.simpleName}")
        LOGGER.warn("- cause : ${e.message}")
        return ErrorResponse(ErrorCode.DUPLICATE_SUBNET,
            "이미 존재하는 서브넷 주소입니다!",
            "이미 해당 서브넷 주소는 서비스에 등록되어있습니다."
        ).badRequest().toMono()
    }

    @ExceptionHandler(MalformedSubnetException::class)
    fun handle(e: MalformedSubnetException): Mono<ResponseEntity<ErrorResponse>> {
        LOGGER.warn("error occurred during handle request!")
        LOGGER.warn("- error : ${e.javaClass.simpleName}")
        LOGGER.warn("- cause : ${e.message}")
        return ErrorResponse(
            ErrorCode.MALFORMED_SUBNET,
            "서브넷 형식이 올바르지 않습니다",
            "서브넷 은 0~255까지의 정수 3개로 구성되어있습니다. (ex, 192.168.0)"
        ).badRequest().toMono()
    }

    @ExceptionHandler(UnknownSubnetException::class)
    fun handle(e: UnknownSubnetException): Mono<ResponseEntity<ErrorResponse>> {
        LOGGER.warn("error occurred during handle request!")
        LOGGER.warn("- error : ${e.javaClass.simpleName}")
        LOGGER.warn("- cause : ${e.message}")
        return ErrorResponse(ErrorCode.UNKNOWN_SUBNET, "서브넷을 찾을 수 없습니다.", "존재하지 않는 서브넷입니다.")
            .badRequest()
            .toMono()
    }

    @ExceptionHandler(PermissionDeniedException::class)
    fun handle(e: PermissionDeniedException): Mono<ResponseEntity<ErrorResponse>> {
        LOGGER.warn("error occurred during handle request!")
        LOGGER.warn("- error : ${e.javaClass.simpleName}")
        LOGGER.warn("- cause : ${e.message}")
        return ErrorResponse(ErrorCode.PERMISSION_DENIED,
            "권한이 없습니다!",
            "해당 기능에 접근하실 권한이 없습니다!"
        ).badRequest().toMono()
    }
}

private fun ErrorResponse.badRequest() = ResponseEntity.badRequest().body(this)
