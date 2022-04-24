package com.iplease.server.subnet.manage.data.entity

data class SubnetInfo(
    val subnet: Subnet,
    val uuid: Long,
    val issuerUuid: Long
)