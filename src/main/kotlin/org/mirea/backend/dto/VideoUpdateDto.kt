package org.mirea.backend.dto

import org.mirea.backend.entities.VideoEntity
import org.mirea.backend.utils.ids.UserID
import org.mirea.backend.utils.ids.VideoID
import org.springframework.web.multipart.MultipartFile

data class VideoUpdateDto(
    val name: String,
) {
    fun toEntity(entity: VideoEntity) = VideoEntity(
        id = entity.id,
        name = name,
        userId = entity.userId,
        preview = entity.preview,
        videoUrl = entity.videoUrl,
    )
}