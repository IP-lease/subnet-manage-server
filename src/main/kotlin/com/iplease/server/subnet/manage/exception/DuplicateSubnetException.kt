package com.iplease.server.subnet.manage.exception

import com.iplease.server.subnet.manage.data.entity.Subnet

class DuplicateSubnetException(val issuerUuid: Long, val subnet: Subnet) : RuntimeException()
