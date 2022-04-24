package com.iplease.server.subnet.manage.service

import com.iplease.server.subnet.manage.entity.Subnet

interface SubnetManageService {
    fun add(issuerUuid: Long, subnet: Subnet)
}
