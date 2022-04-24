package com.iplease.server.subnet.manage.service

import com.iplease.server.subnet.manage.data.entity.Subnet
import com.iplease.server.subnet.manage.data.entity.SubnetInfo
import reactor.core.publisher.Mono

interface SubnetManageService {
    fun add(issuerUuid: Long, subnet: Subnet): Mono<SubnetInfo>
}
