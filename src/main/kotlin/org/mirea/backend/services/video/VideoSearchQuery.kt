package org.mirea.backend.services.video

import org.mirea.backend.dto.VideoPaginationOptions
import org.mirea.backend.repositories.video.queries.VideoRepositorySearchQuery
import org.mirea.backend.utils.ids.UserID
import org.mirea.backend.utils.ids.VideoID

data class VideoSearchQuery(
    val name: String? = null,
    val authorID: Long? = null,
    val paginationOptions: VideoPaginationOptions = VideoPaginationOptions(),
) {
    fun toRepositoryQuery() = VideoRepositorySearchQuery.create {
        name = this@VideoSearchQuery.name
        userID = authorID?.let { UserID(it) }
        paginationData = paginationOptions.toPaginationData()
    }
}