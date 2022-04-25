package com.iplease.server.subnet.manage.service

import com.iplease.server.subnet.manage.data.dto.SubnetDto
import com.iplease.server.subnet.manage.data.table.SubnetInfoTable
import com.iplease.server.subnet.manage.exception.DuplicateSubnetException
import com.iplease.server.subnet.manage.repository.SubnetRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class SubnetManageServiceImpl(private val subnetRepository: SubnetRepository) : SubnetManageService {
    override fun add(issuerUuid: Long, subnet: SubnetDto) =
        subnetRepository.existsBySubnet(subnet)
            .flatMap {
                if(it)  Mono.error(DuplicateSubnetException(issuerUuid, subnet))
                else subnetRepository.save(SubnetInfoTable(0, issuerUuid, subnet.first, subnet.second, subnet.third))
            }.map { it.toSubnetInfo() }
}