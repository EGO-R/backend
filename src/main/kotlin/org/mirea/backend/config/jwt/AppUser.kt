package org.mirea.backend.config.jwt

import org.mirea.backend.entities.user.UserRole
import org.mirea.backend.utils.ids.UserID
import java.security.Principal

data class AppUser(
    val id: UserID,
    val email: String,
    val role: UserRole,
) : Principal {
    override fun getName() = email
}