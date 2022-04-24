package com.iplease.server.subnet.manage.exception

class MalformedSubnetException(val subnet: String) : RuntimeException("잘못된 서브넷 주소입니다. - $subnet")