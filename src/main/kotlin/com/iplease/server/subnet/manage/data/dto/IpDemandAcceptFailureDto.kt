package com.iplease.server.subnet.manage.data.dto

data class IpDemandAcceptFailureDto (
    val demandUuid: Long,
    val issuerUuid: Long,
    val managerUuid: Long,
    val demandedIp: String
)