package com.iplease.server.subnet.manage.exception

import com.iplease.server.subnet.manage.data.dto.SubnetDto

class DuplicateSubnetException(val issuerUuid: Long, val subnet: SubnetDto) : RuntimeException()
