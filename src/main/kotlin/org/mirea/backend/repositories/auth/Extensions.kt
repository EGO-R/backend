package org.mirea.backend.repositories.auth

import org.jooq.Record
import org.mirea.backend.entities.auth.AuthEntity
import org.mirea.backend.entities.auth.ProviderType
import org.mirea.backend.entities.auth.UserWithAuthInfo
import org.mirea.backend.entities.user.UserEntity
import org.mirea.backend.entities.user.UserRole
import org.mirea.backend.jooq.generated.Tables.AUTH_PROVIDER
import org.mirea.backend.jooq.generated.Tables.CLIENTS
import org.mirea.backend.jooq.generated.tables.records.AuthProviderRecord
import org.mirea.backend.utils.ids.AuthID
import org.mirea.backend.utils.ids.UserID

fun AuthProviderRecord.toEntity() = AuthEntity(
    id = AuthID(get(AUTH_PROVIDER.ID)!!),
    userId = UserID(get(AUTH_PROVIDER.USER_ID)!!),
    providerType = ProviderType.from(get(AUTH_PROVIDER.PROVIDER_TYPE)!!),
    providerUserId = get(AUTH_PROVIDER.PROVIDER_USER_ID),
    credentials = get(AUTH_PROVIDER.CREDENTIALS)!!,
)

fun Record.toUserWithAuthEntity() = UserWithAuthInfo(
    user = UserEntity(
        id = UserID(get(CLIENTS.ID)!!),
        displayName = get(CLIENTS.NAME)!!,
        email = get(CLIENTS.EMAIL)!!,
        role = UserRole.from(get(CLIENTS.ROLE)),
    ),
    authInfo = AuthEntity(
        id = AuthID(get(AUTH_PROVIDER.ID)!!),
        userId = UserID(get(AUTH_PROVIDER.USER_ID)!!),
        providerType = ProviderType.from(get(AUTH_PROVIDER.PROVIDER_TYPE)!!),
        providerUserId = get(AUTH_PROVIDER.PROVIDER_USER_ID),
        credentials = get(AUTH_PROVIDER.CREDENTIALS)!!,
    ),
)
