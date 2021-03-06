package com.iplease.server.subnet.manage.controller

import com.iplease.server.subnet.manage.data.dto.SubnetDto
import com.iplease.server.subnet.manage.data.dto.SubnetInfoDto
import com.iplease.server.subnet.manage.exception.MalformedSubnetException
import com.iplease.server.subnet.manage.exception.PermissionDeniedException
import com.iplease.server.subnet.manage.service.SubnetManageService
import com.iplease.server.subnet.manage.data.type.Permission
import com.iplease.server.subnet.manage.data.type.Role
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.Random

class SubnetControllerTest {
    private lateinit var subnetManageService: SubnetManageService
    private lateinit var subnetController: SubnetController

    @BeforeEach @DisplayName("테스트 데이터 초기화")
    fun setUp() {
        //DI 로 주입받을 빈을 mocking 한다.
        subnetManageService = mock<SubnetManageService> {}
        //테스트할 컨트롤러를 초기화한다.
        subnetController = SubnetController(subnetManageService)
    }

    @Test @DisplayName("서브넷 삭제 테스트 - 삭제 성공")
    fun removeSubnetSuccess() {
        //테스트 데이터를 설정한다.
        val issuerUuid = Random().nextLong()
        val subnet = getRandomSubnet()
        whenever(subnetManageService.remove(any())).thenReturn(Mono.empty())

        subnetController.removeSubnet(subnet, issuerUuid, Role.ADMINISTRATOR).block()
        //서브넷 삭제 요청이 서비스단까지 정상적으로 도달하였는지 검사한다.
        verify(subnetManageService, times(1)).remove(subnet.toSubnet())
    }

    @Test @DisplayName("서브넷 추가 테스트 - 추가 성공")
    fun addSubnetSuccess() {
        //테스트 데이터를 설정한다.
        val issuerUuid = Random().nextLong()
        val subnetUuid = Random().nextLong()
        val subnet = getRandomSubnet()
        whenever(subnetManageService.add(issuerUuid, subnet.toSubnet())).thenReturn(SubnetInfoDto(subnet.toSubnet(), subnetUuid, issuerUuid).toMono())
        //서브넷 추가 요청을 처리한다.
        val response = subnetController.addSubnet(subnet, issuerUuid, Role.ADMINISTRATOR)
        //성공적으로 서브넷 추가 완료 응답을 반환하였는지 검사한다.
        assert(response.block()!!.statusCode.is2xxSuccessful)
        val body = response.block()!!.body!!
        assert(body.subnet == subnet.toSubnet())
        assert(body.issuerUuid == issuerUuid)
        assert(body.uuid == subnetUuid)
        //서브넷 추가 요청이 서비스단까지 정상적으로 도달하였는지 검사한다.
        verify(subnetManageService, times(1)).add(issuerUuid, subnet.toSubnet())
    }

    @Test @DisplayName("서브넷 삭제 테스트 - 서브넷 주소가 올바르지 않을 경우")
    fun removeSubnetFailureMalformedSubnet() {
        //테스트 데이터를 설정한다.
        val issuerUuid = Random().nextLong()
        val overflowSubnet = (256..Int.MAX_VALUE).let { "${it.random()}.${it.random()}.${it.random()}" }
        val underflowSubnet = (Int.MIN_VALUE..-1).let { "${it.random()}.${it.random()}.${it.random()}" }
        val malformedSubnet = "helloworld"
        //형식에 맞지 않는 서브넷을 추가하려할 때, 예외가 발생하는지 검사한다.
        assert(assertThrows<MalformedSubnetException> { subnetController.removeSubnet(overflowSubnet, issuerUuid, Role.ADMINISTRATOR).block() }.subnet == overflowSubnet)
        assert(assertThrows<MalformedSubnetException> { subnetController.removeSubnet(underflowSubnet, issuerUuid, Role.ADMINISTRATOR).block() }.subnet == underflowSubnet)
        assert(assertThrows<MalformedSubnetException> { subnetController.removeSubnet(malformedSubnet, issuerUuid, Role.ADMINISTRATOR).block() }.subnet == malformedSubnet)

        verify(subnetManageService, times(0)).remove(any())
    }

    @Test @DisplayName("서브넷 추가 테스트 - 서브넷 주소가 올바르지 않을 경우")
    fun addSubnetFailureMalformedSubnet() {
        //테스트 데이터를 설정한다.
        val issuerUuid = Random().nextLong()
        val overflowSubnet = (256..Int.MAX_VALUE).let { "${it.random()}.${it.random()}.${it.random()}" }
        val underflowSubnet = (Int.MIN_VALUE..-1).let { "${it.random()}.${it.random()}.${it.random()}" }
        val malformedSubnet = "helloworld"
        //형식에 맞지 않는 서브넷을 추가하려할 때, 예외가 발생하는지 검사한다.
        assert(assertThrows<MalformedSubnetException> { subnetController.addSubnet(overflowSubnet, issuerUuid, Role.ADMINISTRATOR).block() }.subnet == overflowSubnet)
        assert(assertThrows<MalformedSubnetException> { subnetController.addSubnet(underflowSubnet, issuerUuid, Role.ADMINISTRATOR).block() }.subnet == underflowSubnet)
        assert(assertThrows<MalformedSubnetException> { subnetController.addSubnet(malformedSubnet, issuerUuid, Role.ADMINISTRATOR).block() }.subnet == malformedSubnet)

        verify(subnetManageService, times(0)).add(any(), any())
    }

    @Test @DisplayName("서브넷 삭제 테스트 - 권한이 없을 경우")
    fun removeSubnetFailurePermissionDenied() {
        //테스트 데이터를 설정한다.
        val issuerUuid = Random().nextLong()
        val subnet = getRandomSubnet()
        //권한이 없을 경우, 예외가 발생하는지 검사한다.
        val exception = assertThrows<PermissionDeniedException> { subnetController.removeSubnet(subnet, issuerUuid, Role.USER).block() }
        assert(exception.role == Role.USER)
        assert(exception.permission == Permission.SUBNET_REMOVE)

        verify(subnetManageService, times(0)).remove(any())
    }

    @Test @DisplayName("서브넷 추가 테스트 - 권한이 없을 경우")
    fun addSubnetFailurePermissionDenied() {
        //테스트 데이터를 설정한다.
        val issuerUuid = Random().nextLong()
        val subnet = getRandomSubnet()
        //권한이 없을 경우, 예외가 발생하는지 검사한다.
        val exception = assertThrows<PermissionDeniedException> { subnetController.addSubnet(subnet, issuerUuid, Role.USER).block() }
        assert(exception.role == Role.USER)
        assert(exception.permission == Permission.SUBNET_ADD)

        verify(subnetManageService, times(0)).add(any(), any())
    }

    private fun getRandomSubnet() = (0..255).let { "${it.random()}.${it.random()}.${it.random()}" }

    private fun String.toSubnet(): SubnetDto =
        this.split(".")
            .map { it.toInt() }
            .map { if(it > 255) throw IllegalArgumentException("잘못된 서브넷 주소입니다.") else it }
            .let { SubnetDto(it[0], it[1], it[2]) }
}
