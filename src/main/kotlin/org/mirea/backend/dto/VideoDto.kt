package org.mirea.backend.dto

import org.mirea.backend.entities.UserEntity
import org.mirea.backend.entities.VideoEntity

data class VideoDto(
    val id: Long,
    val name: String,
    val author: AuthorDto,
)

fun VideoEntity.toDto(author: UserEntity) = VideoDto(
    id = id.value,
    name = name,
    author = author.toAuthorDto(),
)
