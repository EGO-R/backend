package org.mirea.backend.dto

import org.mirea.backend.repositories.shared.SortDirection
import org.mirea.backend.repositories.video.queries.VideoIdPaginationData
import org.mirea.backend.utils.ids.VideoID

data class VideoPaginationOptions(
    val sortField: VideoSortField = VideoSortField.ID,
    val direction: SortDirection = SortDirection.DESC,
    val size: Int? = null,
    val lastSelectedValue: String? = null,
) {
    fun toPaginationData() = when (sortField) {
        VideoSortField.ID -> VideoIdPaginationData.create(
            lastSelectedID = lastSelectedValue?.let { VideoID(it.toLong()) },
        ) {
            size = this@VideoPaginationOptions.size
            sortDirection = direction
        }
    }
}

enum class VideoSortField {
    ID,
    ;
}
