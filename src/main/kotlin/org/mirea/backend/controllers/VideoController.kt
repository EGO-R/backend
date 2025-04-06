package org.mirea.backend.controllers

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

    @PostMapping("/create")
    suspend fun create(@ModelAttribute dto: VideoUpdateDto): VideoDto {
        return videoService.create(dto)
    }

    @PostMapping("/{id}/update")
    suspend fun update(@PathVariable id: VideoID, @RequestBody dto: VideoUpdateDto): VideoDto {
        return videoService.update(id, dto)
    }

    @GetMapping("/check_queue")
    suspend fun checkQueue() {
        while (queue.isNotEmpty()) {
            val dto = queue.take()
            println("Queue element size: ${dto.videoFile?.size}")
        }
    }
}