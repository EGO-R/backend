package org.mirea.backend.repositories.refresh

import org.jooq.DSLContext
import org.jooq.TableField
import org.mirea.backend.entities.refresh.RefreshTokenEntity
import org.mirea.backend.jooq.generated.Tables.REFRESH_TOKEN
import org.mirea.backend.jooq.generated.tables.records.RefreshTokenRecord
import org.mirea.backend.utils.repositories.TableMapper
import org.springframework.stereotype.Component

@Component
class RefreshMapper(
    dsl: DSLContext,
) : TableMapper<RefreshTokenEntity, RefreshTokenRecord>(dsl) {
    override val updateIgnoreFields = setOf(
        REFRESH_TOKEN.USER_ID,
    )

    override fun map(entity: RefreshTokenEntity): Map<TableField<RefreshTokenRecord, *>, *> = fields {
        REFRESH_TOKEN.TOKEN_HASH set entity.tokenHash
        REFRESH_TOKEN.USER_ID set entity.userId.value
        REFRESH_TOKEN.EXPIRES_AT set entity.expiresAt
    }
}
