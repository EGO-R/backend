package org.mirea.backend.dto.user

import org.mirea.backend.entities.user.UserEntity
import org.mirea.backend.utils.ids.UserID

data class UserDto(
    val id: UserID,
    val email: String,
    val name: String,
)

fun UserEntity.update(dto: UserDto) = this.copy(
    displayName = dto.name,
    email = dto.email,
)

fun UserEntity.toDto() = UserDto(
    id, email, displayName
)
