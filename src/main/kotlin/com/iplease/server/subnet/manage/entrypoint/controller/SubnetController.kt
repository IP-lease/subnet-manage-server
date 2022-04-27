package com.iplease.server.subnet.manage.entrypoint.controller

import com.iplease.server.subnet.manage.data.dto.SubnetDto
import com.iplease.server.subnet.manage.data.dto.SubnetInfoDto
import com.iplease.server.subnet.manage.exception.MalformedSubnetException
import com.iplease.server.subnet.manage.exception.PermissionDeniedException
import com.iplease.server.subnet.manage.service.SubnetManageService
import com.iplease.server.subnet.manage.data.type.Permission
import com.iplease.server.subnet.manage.data.type.Role
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/subnet")
class SubnetController(
    private val subnetManageService: SubnetManageService
) {
    //TODO 나중에 AOP 도입 고민해보기
    val LOGGER = LoggerFactory.getLogger(SubnetController::class.java)

    @DeleteMapping("/{subnet}")
    fun removeSubnet(@PathVariable subnet: String,
                     @RequestHeader(name = "X-Login-Account-Uuid") uuid: Long,
                     @RequestHeader(name = "X-Login-Account-Role") role: Role
    ): Mono<ResponseEntity<Unit>> {
        LOGGER.info("request listened!")
        LOGGER.info("- handler : removeSubnet()")
        LOGGER.info("- subnet : $subnet")
        checkPermission(role, Permission.SUBNET_REMOVE) //권한이 있는지 확인한다.
        checkSubnet(subnet) //입력된 subnet이 올바른지 확인한다.
        return subnetManageService.remove(subnet.toSubnetDto()) //서브넷을 제거한다.
            .map {
                LOGGER.info("subnet remove complete! - $subnet")
                ResponseEntity.ok().build()
            }
    }

    @PostMapping("/{subnet}")
    fun addSubnet(@PathVariable subnet: String,
                  @RequestHeader(name = "X-Login-Account-Uuid") uuid: Long,
                  @RequestHeader(name = "X-Login-Account-Role") role: Role
    ): Mono<ResponseEntity<SubnetInfoDto>> {
        LOGGER.info("request listened!")
        LOGGER.info("- handler : addSubnet()")
        LOGGER.info("- subnet : $subnet")
        checkPermission(role, Permission.SUBNET_ADD) //권한이 있는지 확인한다.
        checkSubnet(subnet) //입력된 subnet이 올바른지 확인한다.
        return subnetManageService.add(uuid, subnet.toSubnetDto()) //서브넷을 추가한다.
            .map {
                LOGGER.info("subnet add complete! - $subnet")
                ResponseEntity.ok(it)
            }
    }

    private fun checkPermission(role: Role, permission: Permission) {
        if (!role.hasPermission(permission)) //만약 서브넷 추가 권한이 없다면
            throw PermissionDeniedException(role, permission) //예외를 던진다.
        LOGGER.trace("permission checking complete!")
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
        LOGGER.trace("subnet checking complete!")
    }

    private fun String.toSubnetDto() =
        this.split(".")
            .map { it.toInt() }
            .let { SubnetDto(it[0], it[1], it[2]) }
}