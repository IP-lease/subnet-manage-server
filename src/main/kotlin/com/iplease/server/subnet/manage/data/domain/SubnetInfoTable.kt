package com.iplease.server.subnet.manage.data.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("subnet_info")
data class SubnetInfoTable (
    @Id val uuid: Long,
    val issuerUuid: Long,
    val subnetFirst: Int,
    val subnetSecond: Int,
    val subnetThird: Int
)