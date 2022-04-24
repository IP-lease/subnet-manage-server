package com.iplease.server.subnet.manage.data.table

import com.iplease.server.subnet.manage.data.entity.Subnet
import com.iplease.server.subnet.manage.data.entity.SubnetInfo
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("subnet")
data class SubnetTable (
    @Id val uuid: Long,
    val issuerUuid: Long,
    val subnetFirst: Int,
    val subnetSecond: Int,
    val subnetThird: Int
) {
    fun toSubnetInfo() = SubnetInfo(toSubnet(), uuid, issuerUuid)
    fun toSubnet() = Subnet(subnetFirst, subnetSecond, subnetThird)
}