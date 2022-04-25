package com.iplease.server.subnet.manage.service

import com.iplease.server.subnet.manage.data.dto.LoginAccountDto

interface LoginAccountService {
    fun getLoginAccount(): LoginAccountDto
}
