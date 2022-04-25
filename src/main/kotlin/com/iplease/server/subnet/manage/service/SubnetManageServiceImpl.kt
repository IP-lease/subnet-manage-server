package com.iplease.server.subnet.manage.service

import com.iplease.server.subnet.manage.data.dto.SubnetDto
import com.iplease.server.subnet.manage.data.dto.SubnetInfoDto
import com.iplease.server.subnet.manage.data.mapper.SubnetInfoMapper
import com.iplease.server.subnet.manage.data.table.SubnetInfoTable
import com.iplease.server.subnet.manage.exception.DuplicateSubnetException
import com.iplease.server.subnet.manage.repository.SubnetRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class SubnetManageServiceImpl(
    private val subnetRepository: SubnetRepository,
    private val subnetInfoMapper: SubnetInfoMapper
    ) : SubnetManageService {
    override fun add(issuerUuid: Long, subnet: SubnetDto): Mono<SubnetInfoDto> {
        val table = SubnetInfoTable(0, issuerUuid, subnet.first, subnet.second, subnet.third)
        return subnetRepository.existsBySubnet(subnet)
            .flatMap {
                if (it) Mono.defer { Mono.error(DuplicateSubnetException(issuerUuid, subnet)) }
                else subnetRepository.save(table)
            }.map(subnetInfoMapper::toSubnetInfoDto)
    }

    private fun SubnetRepository.existsBySubnet(subnet: SubnetDto) =
        existsBySubnetFirstAndSubnetSecondAndSubnetThird(subnet.first, subnet.second, subnet.third)
}