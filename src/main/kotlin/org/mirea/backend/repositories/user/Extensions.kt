package org.mirea.backend.repositories.user

import org.jooq.impl.DSL
import org.mirea.backend.jooq.generated.Tables.CLIENTS
import org.mirea.backend.utils.repositories.andIn
import org.mirea.backend.utils.repositories.asUserID

fun UserRepositorySearchQuery.toCondition() = DSL.noCondition()
    .andIn(CLIENTS.ID.asUserID(), ids)