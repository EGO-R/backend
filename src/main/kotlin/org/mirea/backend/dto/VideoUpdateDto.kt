package org.mirea.backend.dto

import org.mirea.backend.entities.VideoEntity
import org.mirea.backend.utils.ids.UserID
import org.mirea.backend.utils.ids.VideoID
import org.springframework.web.multipart.MultipartFile

data class VideoUpdateDto(
    val name: String,
    val preview: String,
    val videoFile: MultipartFile? = null,
) {
    fun toEntity(id: VideoID, userID: UserID) = VideoEntity(
        id = id,
        name = name,
        userId = userID,
        preview = preview,
    )
}