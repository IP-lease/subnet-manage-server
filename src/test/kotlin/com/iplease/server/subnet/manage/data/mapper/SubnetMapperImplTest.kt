package com.iplease.server.subnet.manage.data.mapper

import com.iplease.server.subnet.manage.data.dto.SubnetDto
import com.iplease.server.subnet.manage.data.dto.SubnetInfoDto
import com.iplease.server.subnet.manage.data.table.SubnetInfoTable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.random.Random

class SubnetMapperImplTest {
    private val subnetMapperImpl = SubnetMapperImpl()
    private lateinit var subnetInfoTable: SubnetInfoTable
    private lateinit var subnetInfoDto: SubnetInfoDto
    private lateinit var subnetDto: SubnetDto

    @BeforeEach @DisplayName("테스트 데이터 초기화")
    fun setUp() {
        val subnetUuid = Random.nextLong()
        val issuerUuid = Random.nextLong()
        val subnetFirst = (0..255).random()
        val subnetSecond = (0..255).random()
        val subnetThird = (0..255).random()

        subnetInfoTable = SubnetInfoTable(subnetUuid, issuerUuid, subnetFirst, subnetSecond, subnetThird)
        subnetInfoDto = SubnetInfoDto(SubnetDto(subnetFirst, subnetSecond, subnetThird), subnetUuid, issuerUuid)
        subnetDto = SubnetDto(subnetFirst, subnetSecond, subnetThird)
    }

    @Test @DisplayName("SubnetDTO 로의 변환 테스트")
    fun toSubnetDto() {
        subnetMapperImpl.toSubnetDto(subnetInfoTable).let {
            assert(it.first == subnetInfoTable.subnetFirst)
            assert(it.second == subnetInfoTable.subnetSecond)
            assert(it.third == subnetInfoTable.subnetThird)
        }
    }

    @Test @DisplayName("SubnetInfoDTO 로의 변환 테스트")
    fun toSubnetInfoDto() {
        subnetMapperImpl.toSubnetInfoDto(subnetInfoTable).let {
            assert(it.subnet.first == subnetInfoTable.subnetFirst)
            assert(it.subnet.second == subnetInfoTable.subnetSecond)
            assert(it.subnet.third == subnetInfoTable.subnetThird)
            assert(it.uuid == subnetInfoTable.uuid)
            assert(it.issuerUuid == subnetInfoTable.issuerUuid)
        }
    }

    @Test @DisplayName("SubnetInfoTable 로의 변환 테스트")
    fun toSubnetInfoTable() {
        subnetMapperImpl.toSubnetInfoTable(subnetInfoDto).let {
            assert(it.subnetFirst == subnetInfoDto.subnet.first)
            assert(it.subnetSecond == subnetInfoDto.subnet.second)
            assert(it.subnetThird == subnetInfoDto.subnet.third)
            assert(it.uuid == subnetInfoDto.uuid)
            assert(it.issuerUuid == subnetInfoDto.issuerUuid)
        }
    }
}