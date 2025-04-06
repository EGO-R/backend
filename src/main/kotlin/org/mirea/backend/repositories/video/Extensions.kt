package org.mirea.backend.repositories.video

import org.jooq.impl.DSL
import org.mirea.backend.jooq.generated.Tables.VIDEO
import org.mirea.backend.repositories.video.queries.VideoRepositorySearchQuery
import org.mirea.backend.utils.repositories.andEq
import org.mirea.backend.utils.repositories.andLike
import org.mirea.backend.utils.repositories.asUserID
import org.mirea.backend.utils.repositories.asVideoID

fun VideoRepositorySearchQuery.toCondition() = DSL.noCondition()
    .andEq(VIDEO.ID.asVideoID(), id)
    .andLike(VIDEO.NAME, name)
    .andEq(VIDEO.USER_ID.asUserID(), userID)
