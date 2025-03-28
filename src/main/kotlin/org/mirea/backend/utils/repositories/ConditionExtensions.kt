package org.mirea.backend.utils.repositories

import org.jooq.Condition
import org.jooq.Field
import org.mirea.backend.utils.ids.UserID
import org.mirea.backend.utils.ids.VideoID

fun Condition.andEq(field: Field<UserID>, value: UserID?) =
    if (value != null) {
        this.and(field.eq(value))
    } else {
        this
    }

fun Condition.andEq(field: Field<VideoID>, value: VideoID?) =
    if (value != null) {
        this.and(field.eq(value))
    } else {
        this
    }

fun Condition.andLike(field: Field<String>, value: String?) =
    if (value != null) {
        this.and(field.like("%$value%"))
    } else {
        this
    }
