package org.mirea.backend.repositories.video

import org.jooq.DSLContext
import org.mirea.backend.entities.video.VideoEntity
import org.mirea.backend.jooq.generated.Tables.VIDEO
import org.mirea.backend.jooq.generated.tables.records.VideoRecord
import org.mirea.backend.utils.ids.VideoID
import org.mirea.backend.utils.repositories.TableMapper
import org.springframework.stereotype.Component

@Component
class VideoMapper(
    dsl: DSLContext,
) : TableMapper<VideoEntity, VideoRecord>(dsl) {
    override val updateIgnoreFields = setOf(VIDEO.USER_ID)

    override fun map(entity: VideoEntity) = fields {
        if (entity.id != VideoID.EMPTY) {
            VIDEO.ID set entity.id.value
        }
        VIDEO.NAME set entity.name
        VIDEO.USER_ID set entity.userId.value
        VIDEO.PREVIEW set entity.preview
        VIDEO.VIDEO_URL set entity.videoUrl
    }
}