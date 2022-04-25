package com.iplease.server.subnet.manage.data.table

import com.iplease.server.subnet.manage.data.dto.SubnetDto
import com.iplease.server.subnet.manage.data.dto.SubnetInfoDto
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("subnet_info")
data class SubnetInfoTable (
    @Id val uuid: Long,
    val issuerUuid: Long,
    val subnetFirst: Int,
    val subnetSecond: Int,
    val subnetThird: Int
) {
    fun toSubnetInfo() = SubnetInfoDto(toSubnet(), uuid, issuerUuid)
    fun toSubnet() = SubnetDto(subnetFirst, subnetSecond, subnetThird)
}