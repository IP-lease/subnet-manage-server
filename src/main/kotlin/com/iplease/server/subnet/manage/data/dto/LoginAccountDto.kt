package com.iplease.server.subnet.manage.data.dto

import com.iplease.server.subnet.manage.data.type.Role

//로그인 정보를 해석하여 얻은 계정정보
data class LoginAccountDto (
    val uuid: Long, //계정의 UUID
    val role: Role //계정의 역할
)
