package com.iplease.server.subnet.manage.controller

import com.iplease.server.subnet.manage.data.entity.LoginAccount
import com.iplease.server.subnet.manage.data.entity.Subnet
import com.iplease.server.subnet.manage.exception.MalformedSubnetException
import com.iplease.server.subnet.manage.exception.PermissionDeniedException
import com.iplease.server.subnet.manage.service.LoginAccountService
import com.iplease.server.subnet.manage.service.SubnetManageService
import com.iplease.server.subnet.manage.data.type.Permission
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/subnet")
class SubnetController(
    private val loginAccountService: LoginAccountService,
    private val subnetManageService: SubnetManageService
) {
    @RequestMapping("/add/{subnet}")
    fun addSubnet(@PathVariable subnet: String): Mono<ServerResponse> {
        val loginAccount = loginAccountService.getLoginAccount() //로그인된 계정의 정보를 가져온다.
        checkPermission(loginAccount, Permission.SUBNET_ADD) //권한이 있는지 확인한다.
        checkSubnet(subnet) //입력된 subnet이 올바른지 확인한다.
        return subnetManageService.add(loginAccount.uuid, subnet.toSubnet()) //서브넷을 추가한다.
            .flatMap { ServerResponse.ok().bodyValue(it) }
    }

    private fun checkPermission(account: LoginAccount, permission: Permission) {
        if (!account.role.hasPermission(permission)) //만약 서브넷 추가 권한이 없다면
            throw PermissionDeniedException(account.uuid, permission) //예외를 던진다.
    }

    private fun checkSubnet(subnet: String) {
        try {
            subnet.split(".")
                .map { it.toInt() }
                .any { it < 0 || it > 255 }
                .let { if (it) throw Exception() }
        } catch (e: Exception) { //로직 처리중 예외가 발생하면
            throw MalformedSubnetException(subnet) //형식에 맞지 않는 서브넷 주소라 판단하여, 예외를 발생시킨다.
        }
    }

    private fun String.toSubnet() =
        this.split(".")
            .map { it.toInt() }
            .let { Subnet(it[0], it[1], it[2]) }
}