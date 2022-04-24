package com.iplease.server.subnet.manage.repository

import com.iplease.server.subnet.manage.data.entity.Subnet
import com.iplease.server.subnet.manage.data.table.SubnetTable
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface SubnetRepository: R2dbcRepository<SubnetTable, Long> {
    fun existsBySubnet(subnet: Subnet) = existsBySubnetFirstAndSubnetSecondAndSubnetThird(subnet.first, subnet.second, subnet.third)
    fun existsBySubnetFirstAndSubnetSecondAndSubnetThird(subnetFirst: Int, subnetSecond: Int, subnetThird: Int): Mono<Boolean>
}