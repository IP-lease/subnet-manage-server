package com.iplease.server.subnet.manage.data.dto

data class SubnetDto (
    val first: Int,
    val second: Int,
    val third: Int
) {
    override fun toString(): String {
        return "$first.$second.$third"
    }
}