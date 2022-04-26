package com.iplease.server.subnet.manage.exception

class MalformedIpException(ip: String) : RuntimeException("잘못된 IP 주소입니다. - $ip") {

}
