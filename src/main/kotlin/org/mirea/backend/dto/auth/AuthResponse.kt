package org.mirea.backend.dto.auth

import org.mirea.backend.dto.UserDto

data class AuthResponse(
    val token: String,
    val user: UserDto,
)