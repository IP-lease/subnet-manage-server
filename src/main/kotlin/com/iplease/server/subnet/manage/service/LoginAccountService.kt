package com.iplease.server.subnet.manage.service

import com.iplease.server.subnet.manage.entity.LoginAccount

interface LoginAccountService {
    fun getLoginAccount(): LoginAccount
}
