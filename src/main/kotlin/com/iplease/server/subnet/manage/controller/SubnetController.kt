package com.iplease.server.subnet.manage.controller

import com.iplease.server.subnet.manage.entity.LoginAccount
import com.iplease.server.subnet.manage.exception.PermissionDeniedException
import com.iplease.server.subnet.manage.service.LoginAccountService
import com.iplease.server.subnet.manage.type.Permission
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/subnet")
class SubnetController(
    private val loginAccountService: LoginAccountService
) {
    @RequestMapping("/add/{subnet}")
    fun addSubnet(@PathVariable subnet: String): Mono<ServerResponse> {
        val loginAccount = loginAccountService.getLoginAccount() //로그인된 계정의 정보를 가져온다.
        loginAccount.checkPermission(Permission.SUBNET_ADD) //권한이 있는지 확인한다.
        return ServerResponse.ok().build()
    }

    @RequestMapping("/remove/{subnet}")
    fun removeSubnet(@PathVariable subnet: String) {

    }

    private fun LoginAccount.checkPermission(permission: Permission) {
        if(!role.hasPermission(permission)) //만약 서브넷 추가 권한이 없다면
            throw PermissionDeniedException(uuid, permission) //예외를 던진다.
    }
}