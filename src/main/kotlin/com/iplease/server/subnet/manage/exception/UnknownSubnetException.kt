package com.iplease.server.subnet.manage.exception

import com.iplease.server.subnet.manage.data.dto.SubnetDto

class UnknownSubnetException(val subnet: SubnetDto) : RuntimeException("존재하지 않는 서브넷입니다! - $subnet")
