package org.mirea.backend.services.video

import org.mirea.backend.services.video.enums.VideoSortField
import org.mirea.backend.repositories.SortDirection
import org.mirea.backend.repositories.video.queries.VideoIdPaginationData
import org.mirea.backend.repositories.video.queries.VideoRepositorySearchQuery
import org.mirea.backend.utils.ids.UserID
import org.mirea.backend.utils.ids.VideoID

data class VideoSearchQuery(
    val name: String? = null,
    val authorID: Long? = null,
    val sortField: VideoSortField = VideoSortField.ID,
    val sortDirection: SortDirection = SortDirection.DESC,
    val size: Int? = null,
    val lastSelectedValue: String? = null,
) {
    fun toRepositoryQuery() = VideoRepositorySearchQuery.create {
        name = this@VideoSearchQuery.name
        userID = authorID?.let { UserID(it) }

        paginationData = when (sortField) {
            VideoSortField.ID -> VideoIdPaginationData.create(
                lastSelectedID = lastSelectedValue?.let { VideoID(it.toLong()) },
            ) {
                size = this@VideoSearchQuery.size
                sortDirection = this@VideoSearchQuery.sortDirection
            }
        }
    }
}