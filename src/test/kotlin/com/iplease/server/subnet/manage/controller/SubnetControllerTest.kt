package com.iplease.server.subnet.manage.controller

import com.iplease.server.subnet.manage.entity.LoginAccount
import com.iplease.server.subnet.manage.exception.PermissionDeniedException
import com.iplease.server.subnet.manage.service.LoginAccountService
import com.iplease.server.subnet.manage.type.Permission
import com.iplease.server.subnet.manage.type.Role
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.Random

class SubnetControllerTest {
    @Test @DisplayName("서브넷 추가 테스트 - 권한이 없을 경우")
    fun addSubnetFailurePermission() {
        //테스트 데이터를 설정한다.
        val uuid = Random().nextLong()
        val subnet = "134.12.3"
        
        //Controller 에 DI할 LoginAccountService 를 모킹한다.
        val loginAccountService = mock(LoginAccountService::class.java)
        //로그인 계정을 조회하면, 서브넷 추가 권한이 없는 USER 를 반환한다.
        `when`(loginAccountService.getLoginAccount()).thenReturn(LoginAccount(uuid, Role.USER))

        //테스트할 Controller 객체를 생성한다.
        val controller = SubnetController(loginAccountService)

        //권한이 없을 경우, 예외가 발생하는지 검사한다.
        val exception = assertThrows<PermissionDeniedException> { controller.addSubnet(subnet) }
        assert(exception.uuid == uuid)
        assert(exception.permission == Permission.SUBNET_ADD)
    }
}