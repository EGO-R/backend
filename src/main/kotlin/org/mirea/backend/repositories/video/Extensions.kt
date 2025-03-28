package org.mirea.backend.repositories.video

import org.jooq.Record
import org.jooq.SelectConditionStep
import org.jooq.TableField
import org.jooq.impl.DSL
import org.mirea.backend.jooq.generated.Tables.VIDEO
import org.mirea.backend.repositories.shared.PaginationData
import org.mirea.backend.repositories.shared.SortDirection
import org.mirea.backend.repositories.video.queries.VideoRepositorySearchQuery
import org.mirea.backend.utils.repositories.andEq
import org.mirea.backend.utils.repositories.andLike
import org.mirea.backend.utils.repositories.asUserID
import org.mirea.backend.utils.repositories.asVideoID

const val DEFAULT_PAGE_SIZE = 20

fun VideoRepositorySearchQuery.toCondition() = DSL.noCondition()
    .andEq(VIDEO.ID.asVideoID(), id)
    .andLike(VIDEO.NAME, name)
    .andEq(VIDEO.USER_ID.asUserID(), userID)

fun <R : Record, T> SelectConditionStep<R>.paginated(data: PaginationData<R, T>?) = if(data != null) {
    this
        .orderBy(data.getSorted())
        .seek(data.lastSelectedValue)
        .limit(data.size ?: DEFAULT_PAGE_SIZE)
} else {
    this
}
