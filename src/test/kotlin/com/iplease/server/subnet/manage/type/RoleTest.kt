package com.iplease.server.subnet.manage.type

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class RoleTest {
    @Test @DisplayName("권한검사 테스트 - 권한을 소유하고 있을 경우")
    fun hasPermissionTrue() {
        val role = Role.ADMINISTRATOR //ADMINISTRATOR 는 모든 권한을 소유하고있다.
        val permission = Permission.values().random()
        assert(role.hasPermission(permission))
    }

    @Test @DisplayName("권한검사 테스트 - 권한을 소유하고 있지 않을 경우")
    fun hasPermissionFalse() {
        val role = Role.USER //USER 는 SUBNET_ADD 권한을 소유하고있지 않다.
        val permission = Permission.SUBNET_ADD
        assert(!role.hasPermission(permission))
    }
}