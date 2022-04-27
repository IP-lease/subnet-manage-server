package com.iplease.server.subnet.manage.exception

import com.iplease.server.subnet.manage.data.type.Permission
import com.iplease.server.subnet.manage.data.type.Role

//특정 기능에 접근할 권한이 없을경우 발생하는 예외
class PermissionDeniedException(val role: Role, val permission: Permission) : RuntimeException("권한이 없습니다! - $permission")
