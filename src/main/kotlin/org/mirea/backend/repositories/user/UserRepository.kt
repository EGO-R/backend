package org.mirea.backend.repositories.user

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.mirea.backend.entities.UserEntity
import org.mirea.backend.jooq.generated.Tables.CLIENTS
import org.mirea.backend.utils.ids.UserID
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val DSL: DSLContext,
) {
    suspend fun getById(id: UserID) = withContext(Dispatchers.IO) {
        DSL
            .selectFrom(CLIENTS)
            .where(CLIENTS.ID.eq(id.value))
            .fetchOne()
            ?.into(UserEntity::class.java)
    }

    suspend fun search(query: UserRepositorySearchQuery): List<UserEntity> = withContext(Dispatchers.IO) {
        DSL
            .selectFrom(CLIENTS)
            .where(query.toCondition())
            .fetch()
            .into(UserEntity::class.java)
    }
}