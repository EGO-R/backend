package org.mirea.backend.repositories.user

import org.mirea.backend.entities.user.UserEntity
import org.mirea.backend.jooq.generated.Tables.CLIENTS
import org.mirea.backend.repositories.JooqScope
import org.mirea.backend.utils.ids.UserID
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val jooqScope: JooqScope,
    private val mapper: UserMapper,
) {
    suspend fun getById(id: UserID) = search(
        UserRepositorySearchQuery.create {
            ids = setOf(id)
        }
    ).firstOrNull()

    suspend fun getByEmail(email: String) = search(
        UserRepositorySearchQuery.create {
            this.email = email
        }
    ).firstOrNull()

    suspend fun search(query: UserRepositorySearchQuery): List<UserEntity> =
        jooqScope.useDslContext { ctx ->
            ctx
                .selectFrom(CLIENTS)
                .where(query.toCondition())
                .fetch()
                .map { it.toEntity() }
        }

    suspend fun create(entity: UserEntity) =
        jooqScope.useDslContext { ctx ->
            ctx
                .insertInto(CLIENTS)
                .set(mapper.record(entity))
                .returning()
                .fetchOne()!!
                .toEntity()
        }
}