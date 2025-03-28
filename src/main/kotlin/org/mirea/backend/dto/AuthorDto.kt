package org.mirea.backend.dto

import org.mirea.backend.entities.UserEntity

data class AuthorDto(
    val id: Int,
    val name: String,
)

fun UserEntity.toAuthorDto() = AuthorDto(
    id = id.value,
    name = name,
)
