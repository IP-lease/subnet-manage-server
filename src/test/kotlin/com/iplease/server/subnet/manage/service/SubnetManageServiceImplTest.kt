package com.iplease.server.subnet.manage.service

import com.iplease.server.subnet.manage.data.dto.SubnetDto
import com.iplease.server.subnet.manage.data.table.SubnetInfoTable
import com.iplease.server.subnet.manage.exception.DuplicateSubnetException
import com.iplease.server.subnet.manage.repository.SubnetRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import reactor.core.publisher.Mono
import java.util.*
import kotlin.properties.Delegates

class SubnetManageServiceImplTest {
    lateinit var subnetRepository: SubnetRepository
    lateinit var subnetManageService: SubnetManageServiceImpl
    private var uuid by Delegates.notNull<Long>()
    private var issuerUuid by Delegates.notNull<Long>()
    private var first by Delegates.notNull<Int>()
    private var second by Delegates.notNull<Int>()
    private var third by Delegates.notNull<Int>()
    lateinit var table: SubnetInfoTable

    @BeforeEach @DisplayName("테스트 데이터 초기화")
    fun setUp() {
        subnetRepository = mock(SubnetRepository::class.java)
        subnetManageService = SubnetManageServiceImpl(subnetRepository)
        uuid = Random().nextLong()
        issuerUuid = Random().nextLong()
        first = (0..255).random()
        second = (0..255).random()
        third = (0..255).random()
        table = SubnetInfoTable(uuid, issuerUuid, first, second, third)
    }

    @Test @DisplayName("서브넷 추가 테스트 - 추가 성공")
    fun addSubnetSuccess() {
        `when`(subnetRepository.save(table.copy(uuid = 0))).thenReturn(Mono.just(table))
        `when`(subnetRepository.existsBySubnet(table.toSubnet())).thenReturn(Mono.just(false))

        val result = subnetManageService
            .add(issuerUuid, SubnetDto(first, second, third))
            .block()!!

        verify(subnetRepository, times(1)).save(table.copy(uuid = 0))

        assert(result.uuid == uuid)
        assert(result.issuerUuid == issuerUuid)
        assert(result.subnet == SubnetDto(first, second, third))
    }

    @Test @DisplayName("서브넷 추가 테스트 - 이미 해당 서브넷이 존재할 경우")
    fun addSubnetFailureExist() {
        `when`(subnetRepository.save(table.copy(uuid = 0))).thenReturn(Mono.just(table))
        `when`(subnetRepository.existsBySubnet(table.toSubnet())).thenReturn(Mono.just(true)) //이미 존재하는 서브넷에 대한 추가요청

        val exception = assertThrows<DuplicateSubnetException> {
            subnetManageService
                .add(issuerUuid, SubnetDto(first, second, third))
                .block()!! //Mono.error 에서 exception 이 throw 될 수 있도록, block 을 걸어준다.
        }

        assert(exception.subnet == SubnetDto(first, second, third))
        assert(exception.issuerUuid == issuerUuid)
    }
}