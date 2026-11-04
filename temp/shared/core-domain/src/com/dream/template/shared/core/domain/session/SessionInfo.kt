package com.dream.template.shared.core.domain.session

data class SessionInfo(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
)
