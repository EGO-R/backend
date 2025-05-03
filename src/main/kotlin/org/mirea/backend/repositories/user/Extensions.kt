package org.mirea.backend.repositories.user

import org.jooq.impl.DSL
import org.mirea.backend.entities.user.UserEntity
import org.mirea.backend.entities.user.UserRole
import org.mirea.backend.jooq.generated.Tables.CLIENTS
import org.mirea.backend.jooq.generated.tables.records.ClientsRecord
import org.mirea.backend.utils.ids.UserID
import org.mirea.backend.utils.repositories.andIn
import org.mirea.backend.utils.repositories.asUserID

fun UserRepositorySearchQuery.toCondition() = DSL.noCondition()
    .andIn(CLIENTS.ID.asUserID(), ids)

fun ClientsRecord.toEntity() = UserEntity(
    id = UserID(get(CLIENTS.ID)!!),
    displayName = get(CLIENTS.NAME)!!,
    email = get(CLIENTS.EMAIL)!!,
    role = UserRole.from(get(CLIENTS.ROLE)),
)
