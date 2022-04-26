package com.iplease.server.subnet.manage.service

import com.iplease.server.subnet.manage.data.dto.SubnetDto
import com.iplease.server.subnet.manage.data.dto.SubnetInfoDto
import reactor.core.publisher.Mono

interface SubnetManageService {
    fun add(issuerUuid: Long, subnet: SubnetDto): Mono<SubnetInfoDto>
    fun remove(issuerUuid: Long, subnet: SubnetDto)
}
