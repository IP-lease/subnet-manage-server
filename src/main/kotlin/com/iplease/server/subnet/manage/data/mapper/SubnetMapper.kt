package com.iplease.server.subnet.manage.data.mapper

import com.iplease.server.subnet.manage.data.dto.SubnetDto
import com.iplease.server.subnet.manage.data.table.SubnetInfoTable

interface SubnetMapper {
    //TODO SubnetInfoTable to SubnetTable 고민해보기
    fun toSubnetDto(table: SubnetInfoTable): SubnetDto
}