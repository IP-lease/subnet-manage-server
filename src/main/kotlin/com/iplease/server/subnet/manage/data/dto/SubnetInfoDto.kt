package com.iplease.server.subnet.manage.data.dto

data class SubnetInfoDto(
    val subnet: SubnetDto,
    val uuid: Long,
    val issuerUuid: Long
)