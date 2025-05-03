package org.mirea.backend.utils.repositories

import org.jooq.Condition
import org.jooq.Field

private fun <T> T.transformIf(condition: Boolean, cb: T.() -> T) = if (condition) {
    this.cb()
} else {
    this
}

fun <T> Condition.andEqNotNull(field: Field<T>, value: T?) =
    this.transformIf(value != null) { this.and(field.eq(value)) }

fun Condition.andLike(field: Field<String>, value: String?) =
    this.transformIf(value != null) { this.and(field.likeIgnoreCase("%$value%")) }

fun <T> Condition.andIn(field: Field<T>, value: Set<T>?) =
    this.transformIf(value != null) { this.and(field.`in`(value)) }
