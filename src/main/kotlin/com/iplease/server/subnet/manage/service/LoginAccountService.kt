package com.iplease.server.subnet.manage.service

import com.iplease.server.subnet.manage.data.entity.LoginAccount

interface LoginAccountService {
    fun getLoginAccount(): LoginAccount
}
