package org.mirea.backend.controllers

import org.mirea.backend.dto.UploadUrlDto
import org.mirea.backend.dto.VideoCreateDto
import org.mirea.backend.dto.VideoDto
import org.mirea.backend.dto.VideoUpdateDto
import org.mirea.backend.services.video.VideoSearchQuery
import org.mirea.backend.services.video.VideoService
import org.mirea.backend.utils.ids.VideoID
import org.springframework.web.bind.annotation.*
import java.util.concurrent.BlockingQueue

@RestController
@CrossOrigin(origins = ["http://localhost:3000"])
@RequestMapping("/api/videos")
class VideoController(
    private val videoService: VideoService,
    private val queue: BlockingQueue<VideoUpdateDto>,
) {
    @GetMapping
    suspend fun search(@RequestBody query: VideoSearchQuery = VideoSearchQuery()): List<VideoDto> {
        return videoService.search(query)
    }

    @GetMapping("/{id}")
    suspend fun getById(@PathVariable id: VideoID): VideoDto {
        return videoService.getById(id)
    }

    @PostMapping("/create")
    suspend fun create(@ModelAttribute dto: VideoCreateDto): VideoDto {
        return videoService.create(dto)
    }

    @PostMapping("/{id}/update")
    suspend fun update(@PathVariable id: VideoID, @RequestBody dto: VideoUpdateDto): VideoDto {
        return videoService.update(id, dto)
    }

    @GetMapping("/upload")
    suspend fun getUploadUrl(): UploadUrlDto {
        return videoService.getUploadUrl()
    }

    @DeleteMapping("/{id}")
    suspend fun deleteById(@PathVariable id: VideoID) {
        videoService.deleteById(id)
    }
}