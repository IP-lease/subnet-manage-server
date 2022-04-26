package com.iplease.server.subnet.manage.service

import com.iplease.server.subnet.manage.data.dto.SubnetDto
import com.iplease.server.subnet.manage.exception.MalformedIpException
import com.iplease.server.subnet.manage.exception.WrongSubnetIpException
import com.iplease.server.subnet.manage.repository.SubnetRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class IpValidServiceImpl(
    private val subnetRepository: SubnetRepository
): IpValidService {
    override fun checkIp(ip: String) {
        checkIpFormat(ip)
        checkIpSubnet(ip)
    }

    private fun checkIpFormat(ip: String) {
        try {
            ip.split(".")
                .map { it.toInt() }
                .any { it < 0 || it > 255 }
                .let { if (it) throw Exception() }
        } catch (e: Exception) {
            throw MalformedIpException(ip)
        }
    }

    private fun checkIpSubnet(ip: String) {
        if(!ip.split(".")
            .map { it.toInt() }
            .let { SubnetDto(it[0], it[1], it[2]) }
            .let { existsBySubnet(it) }
            .block()!!) throw WrongSubnetIpException(ip)
    }

    private fun existsBySubnet(subnet: SubnetDto): Mono<Boolean> {
        return subnetRepository
            .existsBySubnetFirstAndSubnetSecondAndSubnetThird(subnet.first, subnet.second, subnet.third)
    }
}