package org.mirea.backend.dto

import org.mirea.backend.repositories.shared.SortDirection
import org.mirea.backend.repositories.video.queries.VideoIdPaginationData
import org.mirea.backend.utils.ids.VideoID

data class VideoPaginationOptions(
    val fields: VideoSortFields,
    val direction: SortDirection,
    val size: Int? = null,
    val lastSelectedValue: String,
) {
    fun toPaginationData() = when (fields) {
        VideoSortFields.ID -> VideoIdPaginationData.create(
            lastSelectedID = VideoID(lastSelectedValue.toLong()),
        ) {
            size = this@VideoPaginationOptions.size
            sortDirection = direction
        }
    }
}

enum class VideoSortFields {
    ID,
    ;
}
