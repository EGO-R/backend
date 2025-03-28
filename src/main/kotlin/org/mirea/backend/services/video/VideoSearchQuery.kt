package org.mirea.backend.services.video

import org.mirea.backend.dto.VideoPaginationOptions

data class VideoSearchQuery internal constructor(
    val name: String?,
    val authorID: Int?,
    val paginationOptions: VideoPaginationOptions,
) {
    companion object {
        fun create(cb: VideoSearchQueryBuilder.() -> Unit) =
            VideoSearchQueryBuilder().apply(cb).build()

        class VideoSearchQueryBuilder {
            var name: String? = null
            var authorID: Int? = null

            fun build() = VideoSearchQuery(
                name = name,
                authorID = authorID,
            )
        }
    }
}