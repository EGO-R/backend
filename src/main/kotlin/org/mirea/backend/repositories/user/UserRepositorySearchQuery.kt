package org.mirea.backend.repositories.user

import org.mirea.backend.utils.ids.UserID

data class UserRepositorySearchQuery(
    val ids: Set<UserID>?,
) {
    companion object {
        fun create(cb: UserRepositorySearchQueryBuilder.() -> Unit) =
            UserRepositorySearchQueryBuilder().apply(cb).build()

        class UserRepositorySearchQueryBuilder {
            var ids: Set<UserID>? = null

            fun build() = UserRepositorySearchQuery(
                ids = ids,
            )
        }
    }
}