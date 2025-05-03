package org.mirea.backend.services.video

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import org.mirea.backend.dto.*
import org.mirea.backend.entities.VideoEntity
import org.mirea.backend.exceptions.NoExistingVideoException
import org.mirea.backend.exceptions.UserNotFoundException
import org.mirea.backend.repositories.user.UserRepositorySearchQuery
import org.mirea.backend.repositories.video.VideoRepository
import org.mirea.backend.repositories.video.queries.VideoRepositorySearchQuery
import org.mirea.backend.services.S3Service
import org.mirea.backend.services.DbTransactionManager
import org.mirea.backend.services.UserService
import org.mirea.backend.utils.ids.UserID
import org.mirea.backend.utils.ids.VideoID
import org.springframework.stereotype.Service

@Service
class VideoService(
    private val videoRepository: VideoRepository,
    private val userService: UserService,
    private val s3Service: S3Service,
    private val transactionManager: DbTransactionManager,
) {
    private val logger = LogManager.getLogger()

    suspend fun getUploadUrl(): UploadUrlDto {
        val url = s3Service.generatePreSignedVideoUrl()

        return UploadUrlDto(
            url = url,
        )
    }

    suspend fun getById(id: VideoID): VideoDto {
        val video = videoRepository.getById(id) ?: throw NoExistingVideoException()
        val user = userService.getById(video.userId) ?: throw UserNotFoundException()
        return video.toDto(user)
    }

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

    suspend fun create(dto: VideoCreateDto): VideoDto = transactionManager.transactional {
        val userId = UserID(1)
        val preview = s3Service.uploadPreview(dto.preview)
        val entity = VideoEntity(
            id = VideoID.EMPTY,
            userId = userId,
            name = dto.name,
            preview = preview,
            videoUrl = dto.videoUrl,
        )
        upsert(entity)
    }

    suspend fun update(id: VideoID, dto: VideoUpdateDto): VideoDto {
        val existingVideo = videoRepository.getById(id) ?: throw NoExistingVideoException()
        val newVideo = dto.toEntity(existingVideo)
        return upsert(newVideo)
    }

    private suspend fun upsert(entity: VideoEntity): VideoDto {
        val user = userService.getById(entity.userId)!!
        return videoRepository.upsert(entity).toDto(user)
    }

    suspend fun deleteById(id: VideoID) {
        val video = videoRepository.getById(id) ?: throw NoExistingVideoException()

        coroutineScope {
            launch {
                s3Service.deleteFile(video.preview)
            }
            launch {
                s3Service.deleteFile(video.videoUrl)
            }
            launch {
                val query = VideoRepositorySearchQuery.create {
                    this.id = id
                }
                videoRepository.delete(query)
            }
        }
    }
}