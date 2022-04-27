package com.iplease.server.subnet.manage.data.dto

data class IpDemandAcceptDto(
    val demandUuid: Long,
    val issuerUuid: Long,
    val managerUuid: Long,
    val demandedIp: String
)