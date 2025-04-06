package org.mirea.backend.utils.repositories

import org.jooq.Condition
import org.jooq.Field
import org.mirea.backend.utils.ids.UserID
import org.mirea.backend.utils.ids.VideoID

private fun <T> T.transformIf(condition: Boolean, cb: T.() -> T) = if (condition) {
    this.cb()
} else {
    this
}

fun <T> Condition.andEq(field: Field<T>, value: T?) =
    this.transformIf(value != null) { this.and(field.eq(value)) }

fun Condition.andLike(field: Field<String>, value: String?) =
    this.transformIf(value != null) { this.and(field.like("%$value%")) }

fun <T> Condition.andIn(field: Field<T>, value: Set<T>?) =
    this.transformIf(value != null) { this.and(field.`in`(value)) }
