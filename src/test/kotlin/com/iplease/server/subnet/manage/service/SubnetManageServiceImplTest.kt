package com.iplease.server.subnet.manage.service

import com.iplease.server.subnet.manage.data.dto.SubnetDto
import com.iplease.server.subnet.manage.data.dto.SubnetInfoDto
import com.iplease.server.subnet.manage.data.mapper.SubnetInfoMapper
import com.iplease.server.subnet.manage.data.domain.SubnetInfoTable
import com.iplease.server.subnet.manage.exception.DuplicateSubnetException
import com.iplease.server.subnet.manage.exception.UnknownSubnetException
import com.iplease.server.subnet.manage.repository.SubnetRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import reactor.core.publisher.Mono
import java.util.*
import kotlin.properties.Delegates

class SubnetManageServiceImplTest {
    private lateinit var subnetInfoMapper: SubnetInfoMapper
    private lateinit var subnetRepository: SubnetRepository
    private lateinit var subnetManageService: SubnetManageServiceImpl
    private var uuid by Delegates.notNull<Long>()
    private var issuerUuid by Delegates.notNull<Long>()
    private var first by Delegates.notNull<Int>()
    private var second by Delegates.notNull<Int>()
    private var third by Delegates.notNull<Int>()
    private lateinit var table: SubnetInfoTable

    @BeforeEach @DisplayName("테스트 데이터 초기화")
    fun setUp() {
        subnetInfoMapper = mock<SubnetInfoMapper>{}
        subnetRepository = mock<SubnetRepository>{}
        subnetManageService = SubnetManageServiceImpl(subnetRepository, subnetInfoMapper)
        uuid = Random().nextLong()
        issuerUuid = Random().nextLong()
        first = (0..255).random()
        second = (0..255).random()
        third = (0..255).random()
        table = SubnetInfoTable(uuid, issuerUuid, first, second, third)
    }
    
    @Test @DisplayName("서브넷 삭제 테스트 - 삭제 성공")
    fun removeSubnetSuccess() {
        whenever(subnetRepository.deleteById(uuid)).thenReturn(Mono.empty())
        whenever(subnetRepository.existsBySubnetFirstAndSubnetSecondAndSubnetThird(first, second, third))
            .thenReturn(Mono.just(true))

        subnetManageService.remove(SubnetDto(first, second, third)).subscribe()

        verify(subnetRepository, times(1))
            .deleteBySubnetFirstAndSubnetSecondAndSubnetThird(first, second, third)
    }

    @Test @DisplayName("서브넷 추가 테스트 - 추가 성공")
    fun addSubnetSuccess() {
        whenever(subnetRepository.save(table.copy(uuid = 0))).thenReturn(Mono.just(table))
        whenever(subnetRepository.existsBySubnetFirstAndSubnetSecondAndSubnetThird(first, second, third))
            .thenReturn(Mono.just(false))
        whenever(subnetInfoMapper.toSubnetInfoDto(table)).thenReturn(table.toSubnetInfoDto())
        //whenever(subnetInfoMapper.toSubnetInfoTable(table.toSubnetInfoDto())).thenReturn(table)

        val result = subnetManageService
            .add(issuerUuid, SubnetDto(first, second, third))
            .block()!!

        verify(subnetRepository, times(1)).save(table.copy(uuid = 0))

        assert(result.uuid == uuid)
        assert(result.issuerUuid == issuerUuid)
        assert(result.subnet == SubnetDto(first, second, third))
    }

    @Test @DisplayName("서브넷 삭제 테스트 - 해당 서브넷이 존재하지 않을경우")
    fun removeSubnetFailureNotExist() {
        whenever(subnetRepository.existsBySubnetFirstAndSubnetSecondAndSubnetThird(first, second, third))
            .thenReturn(Mono.just(false)) //존재하지 않는 서브넷에 대한 삭제요청

        val exception = assertThrows<UnknownSubnetException> {
            subnetManageService
                .remove(SubnetDto(first, second, third))
                .block()
        }

        assert(exception.subnet == SubnetDto(first, second, third))
    }

    @Test @DisplayName("서브넷 추가 테스트 - 이미 해당 서브넷이 존재할 경우")
    fun addSubnetFailureExist() {
        whenever(subnetRepository.save(table.copy(uuid = 0))).thenReturn(Mono.just(table))
        whenever(subnetRepository.existsBySubnetFirstAndSubnetSecondAndSubnetThird(first, second, third))
            .thenReturn(Mono.just(true)) //이미 존재하는 서브넷에 대한 추가요청

        val exception = assertThrows<DuplicateSubnetException> {
            subnetManageService
                .add(issuerUuid, SubnetDto(first, second, third))
                .block()!! //Mono.error 에서 exception 이 throw 될 수 있도록, block 을 걸어준다.
        }

        assert(exception.subnet == SubnetDto(first, second, third))
    }

    private fun SubnetInfoTable.toSubnetDto() = SubnetDto(subnetFirst, subnetSecond, subnetThird)
    private fun SubnetInfoTable.toSubnetInfoDto(): SubnetInfoDto = SubnetInfoDto(toSubnetDto(), uuid, issuerUuid)
}

