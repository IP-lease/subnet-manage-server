package com.iplease.server.subnet.manage.data.type

enum class Role(
    vararg roles: Permission //해당 역할이 가진 권한 목록
) {
    USER, //일반 회원 (ex, 학생)
    OPERATOR(Permission.SUBNET_ADD, Permission.SUBNET_REMOVE), //사설IP 관리자 (ex, 담당교사, 전문교육부...)
    ADMINISTRATOR(*Permission.values()); //Iplease 개발팀 (모든 기능 사용가능)

    //생성자 내 가변 파라미터를 통해, 해당 역할이 지닌 권한을 초기화한다.
    private val permissions: Set<Permission> = roles.toSet()

    //이 역할이 해당 권한을 지니고 있는지 확인한다.
    fun hasPermission(permission: Permission) = permissions.contains(permission)
}