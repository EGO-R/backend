package org.mirea.backend.repositories.video

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.mirea.backend.entities.VideoEntity
import org.mirea.backend.jooq.generated.Tables.VIDEO
import org.mirea.backend.repositories.video.queries.VideoRepositorySearchQuery
import org.mirea.backend.utils.ids.VideoID
import org.mirea.backend.utils.repositories.paginated
import org.springframework.stereotype.Repository

@Repository
class VideoRepository(
    private val DSL: DSLContext,
) {
    private val mapper = VideoMapper(DSL)

    suspend fun search(query: VideoRepositorySearchQuery): List<VideoEntity> = withContext(Dispatchers.IO) {
        DSL
            .selectFrom(VIDEO)
            .where(query.toCondition())
            .paginated(query.paginationData)
            .fetch()
            .into(VideoEntity::class.java)
    }

    suspend fun getById(id: VideoID) = search(VideoRepositorySearchQuery.create {
        this.id = id
    }).firstOrNull()

    suspend fun upsert(entity: VideoEntity) = withContext(Dispatchers.IO) {
        DSL
            .insertInto(VIDEO)
            .set(mapper.record(entity))
            .onDuplicateKeyUpdate()
            .set(mapper.updateRecord(entity))
            .returning()
            .fetchOne()
            ?.into(VideoEntity::class.java)!!
    }

    suspend fun delete(query: VideoRepositorySearchQuery): List<VideoID> = withContext(Dispatchers.IO) {
        DSL
            .deleteFrom(VIDEO)
            .where(query.toCondition())
            .returning()
            .fetch(VIDEO.ID)
            .map { VideoID(it) }
    }
}