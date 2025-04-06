package org.mirea.backend.config

import org.mirea.backend.dto.VideoUpdateDto
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

@Configuration
class UploadQueryConfig {
    @Bean
    fun uploadQuery(): BlockingQueue<VideoUpdateDto> {
        return LinkedBlockingQueue()
    }
}