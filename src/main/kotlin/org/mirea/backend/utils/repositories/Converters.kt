package org.mirea.backend.utils.repositories

import org.jooq.Field
import org.mirea.backend.utils.ids.UserID
import org.mirea.backend.utils.ids.VideoID

fun Field<Long>.asUserID() = this.convert(
    UserID::class.java,
    { UserID(it) },
    { it.value },
)

fun Field<Long>.asVideoID() = this.convert(
    VideoID::class.java,
    { VideoID(it) },
    { it.value },
)
