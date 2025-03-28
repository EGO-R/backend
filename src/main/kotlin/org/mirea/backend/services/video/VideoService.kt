package org.mirea.backend.services.video

import org.mirea.backend.dto.VideoDto
import org.mirea.backend.repositories.video.VideoRepository
import org.mirea.backend.services.UserService
import org.springframework.stereotype.Service

@Service
class VideoService(
    private val videoRepository: VideoRepository,
    private val userService: UserService,
) {
    suspend fun search(query: VideoSearchQuery): List<VideoDto> {
        val repositoryQuery = query.
        val videos =
    }
}