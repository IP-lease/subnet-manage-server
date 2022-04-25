package com.iplease.server.subnet.manage.repository

import com.iplease.server.subnet.manage.data.table.SubnetInfoTable
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface SubnetRepository: R2dbcRepository<SubnetInfoTable, Long> {
    fun existsBySubnetFirstAndSubnetSecondAndSubnetThird(subnetFirst: Int, subnetSecond: Int, subnetThird: Int): Mono<Boolean>
}