package com.iplease.server.subnet.manage.exception

class WrongSubnetIpException(val ip: String) : RuntimeException("교내 사설IP 대역 바깥에 존재하는 IP입니다! - $ip")