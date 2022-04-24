package com.iplease.server.subnet.manage.data.entity

import com.iplease.server.subnet.manage.data.type.Role

//로그인 정보를 해석하여 얻은 계정정보
data class LoginAccount (
    val uuid: Long, //계정의 UUID
    val role: Role //계정의 역할
)
