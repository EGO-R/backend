package org.mirea.backend.dto

import org.mirea.backend.entities.user.UserEntity

data class AuthorDto(
    val id: Long,
    val name: String,
)

fun UserEntity.toAuthorDto() = AuthorDto(
    id = id.value,
    name = displayName,
)
