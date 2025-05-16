package org.mirea.backend.repositories.refresh

import org.mirea.backend.entities.refresh.RefreshTokenEntity
import org.mirea.backend.jooq.generated.Tables.REFRESH_TOKEN
import org.mirea.backend.repositories.JooqScope
import org.springframework.stereotype.Repository

@Repository
class RefreshTokenRepository(
    val jooqScope: JooqScope,
    val mapper: RefreshMapper,
) {
    suspend fun searchByToken(tokenHash: String): RefreshTokenEntity? = jooqScope.useDslContext { ctx ->
        ctx
            .selectFrom(REFRESH_TOKEN)
            .where(REFRESH_TOKEN.TOKEN_HASH.eq(tokenHash))
            .fetchOne()
            ?.toEntity()
    }

    suspend fun upsert(entity: RefreshTokenEntity): RefreshTokenEntity = jooqScope.useDslContext { ctx ->
        ctx
            .insertInto(REFRESH_TOKEN)
            .set(mapper.record(entity))
            .onDuplicateKeyUpdate()
            .set(mapper.updateRecord(entity))
            .returning()
            .fetchOne()!!
            .toEntity()
    }
}