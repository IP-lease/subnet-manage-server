package com.iplease.server.subnet.manage.data.mapper

import com.iplease.server.subnet.manage.data.dto.SubnetDto
import com.iplease.server.subnet.manage.data.dto.SubnetInfoDto
import com.iplease.server.subnet.manage.data.table.SubnetInfoTable
import org.springframework.stereotype.Component

@Component //TODO ModelMapper 나 Map Struct 도입 생각해보기
class SubnetAndInfoMapperImpl: SubnetMapper, SubnetInfoMapper {
    override fun toSubnetInfoDto(table: SubnetInfoTable) = SubnetInfoDto(toSubnetDto(table), table.uuid, table.issuerUuid)
    override fun toSubnetDto(table: SubnetInfoTable) = SubnetDto(table.subnetFirst, table.subnetSecond, table.subnetThird)
    override fun toSubnetInfoTable(dto: SubnetInfoDto) = SubnetInfoTable(dto.uuid, dto.issuerUuid, dto.subnet.first, dto.subnet.second, dto.subnet.third)
}