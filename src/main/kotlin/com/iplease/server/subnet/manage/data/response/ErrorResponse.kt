package com.iplease.server.subnet.manage.data.response

import com.iplease.server.subnet.manage.data.type.ErrorCode

data class ErrorResponse (
    val status: ErrorCode,
    val title: String,
    val message: String
)