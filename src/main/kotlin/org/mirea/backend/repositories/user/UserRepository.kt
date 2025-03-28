package org.mirea.backend.repositories.user

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.mirea.backend.entities.UserEntity
import org.mirea.backend.jooq.generated.Tables.CLIENTS
import org.mirea.backend.jooq.generated.tables.Clients
import org.mirea.backend.utils.ids.UserID
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val DSL: DSLContext,
) {
    suspend fun getById(id: UserID) = withContext(Dispatchers.IO) {
        DSL
            .selectFrom(Clients.CLIENTS)
            .where(CLIENTS.ID.eq(id.value))
            .fetchOne()
            ?.into(UserEntity::class.java)!!
    }
}