package org.mirea.backend.repositories.video.queries

import org.mirea.backend.jooq.generated.tables.records.VideoRecord
import org.mirea.backend.repositories.PaginationData
import org.mirea.backend.utils.ids.UserID
import org.mirea.backend.utils.ids.VideoID

data class VideoRepositorySearchQuery internal constructor(
    val id: VideoID?,
    val name: String?,
    val userID: UserID?,
    val paginationData: PaginationData<VideoRecord, *>?,
) {
    companion object {
        fun create(cb: VideoRepositorySearchQueryBuilder.() -> Unit) =
            VideoRepositorySearchQueryBuilder().apply(cb).build()
    }

    class VideoRepositorySearchQueryBuilder {
        var id: VideoID? = null
        var name: String? = null
        var userID: UserID? = null
        var paginationData: PaginationData<VideoRecord, *>? = null

        fun build() = VideoRepositorySearchQuery(
            id = id,
            name = name,
            userID = userID,
            paginationData = paginationData,
        )
    }
}