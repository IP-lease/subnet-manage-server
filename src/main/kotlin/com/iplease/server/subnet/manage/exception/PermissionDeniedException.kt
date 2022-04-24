package com.iplease.server.subnet.manage.exception

import com.iplease.server.subnet.manage.type.Permission

//특정 기능에 접근할 권한이 없을경우 발생하는 예외
class PermissionDeniedException(val uuid: Long, val permission: Permission) : RuntimeException("권한이 없습니다! - $permission")
