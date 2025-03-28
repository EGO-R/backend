package org.mirea.backend.dto

import org.mirea.backend.entities.UserEntity
import org.mirea.backend.utils.ids.UserID

data class UserDto(
    val id: UserID,
    val email: String,
    val name: String,
)

fun UserEntity.update(dto: UserDto) = this.copy(
    name = dto.name,
    email = dto.email,
)

fun UserEntity.toDto() = UserDto(
    id, email, name
)
