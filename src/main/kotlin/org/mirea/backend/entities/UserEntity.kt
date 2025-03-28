package org.mirea.backend.entities

import org.mirea.backend.utils.ids.UserID

data class UserEntity(
    val id: UserID,
    val name: String,
    val email: String,
)