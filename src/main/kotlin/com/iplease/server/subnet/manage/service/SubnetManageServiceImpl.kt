package com.iplease.server.subnet.manage.service

import com.iplease.server.subnet.manage.data.dto.SubnetDto
import com.iplease.server.subnet.manage.data.dto.SubnetInfoDto
import com.iplease.server.subnet.manage.data.mapper.SubnetInfoMapper
import com.iplease.server.subnet.manage.data.domain.SubnetInfoTable
import com.iplease.server.subnet.manage.exception.DuplicateSubnetException
import com.iplease.server.subnet.manage.exception.UnknownSubnetException
import com.iplease.server.subnet.manage.repository.SubnetRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class SubnetManageServiceImpl(
    private val subnetRepository: SubnetRepository,
    private val subnetInfoMapper: SubnetInfoMapper
    ) : SubnetManageService {
    @Transactional
    override fun add(issuerUuid: Long, subnet: SubnetDto): Mono<SubnetInfoDto> =
        subnetRepository.existsBySubnet(subnet)
            .flatMap {
                if (it) Mono.defer { Mono.error(DuplicateSubnetException(subnet)) }
                else subnetRepository.save(
                    SubnetInfoTable(0, issuerUuid, subnet.first, subnet.second, subnet.third))
            }.map(subnetInfoMapper::toSubnetInfoDto)


    override fun remove(subnet: SubnetDto): Mono<Unit> =
        subnetRepository.existsBySubnet(subnet)
            .flatMap {
                if(it) subnetRepository.deleteBySubnet(subnet)
                else Mono.defer { Mono.error(UnknownSubnetException(subnet)) }
            }


    private fun SubnetRepository.existsBySubnet(subnet: SubnetDto): Mono<Boolean> {
        return existsBySubnetFirstAndSubnetSecondAndSubnetThird(subnet.first, subnet.second, subnet.third)
    }
    private fun SubnetRepository.deleteBySubnet(subnet: SubnetDto): Mono<Unit> {
        return deleteBySubnetFirstAndSubnetSecondAndSubnetThird(subnet.first, subnet.second, subnet.third)
    }
}