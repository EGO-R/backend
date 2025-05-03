package org.mirea.backend.entities.user

import org.mirea.backend.utils.ids.UserID
import java.security.Principal

data class UserEntity(
    val id: UserID = UserID.EMPTY,
    val displayName: String,
    val email: String,
    val role: UserRole = UserRole.USER,
) : Principal {
    override fun getName() = email
}
