package com.iplease.server.subnet.manage.exception

import com.iplease.server.subnet.manage.data.dto.SubnetDto

class DuplicateSubnetException(val subnet: SubnetDto) : RuntimeException("이미 존재하는 서브넷입니다! - $subnet")
