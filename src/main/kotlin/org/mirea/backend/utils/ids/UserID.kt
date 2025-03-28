package org.mirea.backend.utils.ids

@JvmInline
value class UserID(val value: Int) {
    companion object {
        val emptyId = 0

        val EMPTY = UserID(emptyId)
    }
}