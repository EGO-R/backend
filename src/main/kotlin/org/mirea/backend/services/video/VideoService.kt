package org.mirea.backend.services.video

import org.apache.logging.log4j.LogManager
import org.mirea.backend.dto.VideoDto
import org.mirea.backend.dto.VideoUpdateDto
import org.mirea.backend.dto.toDto
import org.mirea.backend.exceptions.NoExistingVideoException
import org.mirea.backend.repositories.user.UserRepositorySearchQuery
import org.mirea.backend.repositories.video.VideoRepository
import org.mirea.backend.services.S3Service
import org.mirea.backend.services.UserService
import org.mirea.backend.utils.ids.UserID
import org.mirea.backend.utils.ids.VideoID
import org.springframework.stereotype.Service
import java.util.concurrent.BlockingQueue

@Service
class VideoService(
    private val videoRepository: VideoRepository,
    private val userService: UserService,
    private val s3Service: S3Service,
    private val queue: BlockingQueue<VideoUpdateDto>,
) {
    private val logger = LogManager.getLogger()

    suspend fun search(query: VideoSearchQuery): List<VideoDto> {
        logger.info("Search query: $query")

        val repositoryQuery = query.toRepositoryQuery()
        val videos = videoRepository.search(repositoryQuery)

        val usersQuery = UserRepositorySearchQuery.create {
            ids = videos.mapTo(mutableSetOf()) { it.userId }
        }
        val usersMap = userService.search(usersQuery).associateBy { it.id }

        return videos.mapNotNull { video ->
            usersMap[video.userId]?.let { video.toDto(it) }
        }
    }

    suspend fun create(dto: VideoUpdateDto): VideoDto {
        if (dto.videoFile == null) {
            throw NoExistingVideoException()
        }

        return upsert(VideoID.EMPTY, dto)
    }

    suspend fun update(id: VideoID, dto: VideoUpdateDto): VideoDto {
        val existingVideo = videoRepository.getById(id) ?: throw NoExistingVideoException()
        return upsert(id, dto.copy(videoFile = null))
    }

    private suspend fun upsert(id: VideoID, dto: VideoUpdateDto): VideoDto {
        logger.info("Upserting id: $id, dto: $dto")

        val userID = UserID(1)
        val entity = dto.toEntity(id, userID)
        val user = userService.getById(userID)!!
        if (dto.videoFile != null) {
            s3Service.uploadVideo(dto.videoFile)
        }
        val result =  videoRepository.upsert(entity).toDto(user)
        return result
    }
}