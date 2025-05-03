package org.mirea.backend.entities

import org.mirea.backend.utils.ids.UserID
import org.mirea.backend.utils.ids.VideoID

data class VideoEntity(
    val id: VideoID,
    val name: String,
    val videoUrl: String,
    val userId: UserID,
    val preview: String,
)