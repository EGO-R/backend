package org.mirea.backend.dto

import org.springframework.web.multipart.MultipartFile

data class VideoCreateDto(
    val name: String,
    val preview: MultipartFile,
    val videoUrl: String,
)