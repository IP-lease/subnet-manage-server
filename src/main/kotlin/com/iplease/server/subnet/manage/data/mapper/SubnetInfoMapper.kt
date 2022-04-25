package com.iplease.server.subnet.manage.data.mapper

import com.iplease.server.subnet.manage.data.dto.SubnetInfoDto
import com.iplease.server.subnet.manage.data.table.SubnetInfoTable

interface SubnetInfoMapper {
    fun toSubnetInfoDto(table: SubnetInfoTable): SubnetInfoDto
    fun toSubnetInfoTable(dto: SubnetInfoDto): SubnetInfoTable
}