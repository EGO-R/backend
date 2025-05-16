package org.mirea.backend.repositories.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.mirea.backend.entities.auth.AuthEntity
import org.mirea.backend.entities.auth.ProviderType
import org.mirea.backend.entities.auth.UserWithAuthInfo
import org.mirea.backend.jooq.generated.Tables.AUTH_PROVIDER
import org.mirea.backend.jooq.generated.Tables.CLIENTS
import org.mirea.backend.repositories.JooqScope
import org.mirea.backend.utils.ids.UserID
import org.mirea.backend.utils.repositories.andEqNotNull
import org.springframework.stereotype.Repository

@Repository
class AuthRepository(
    private val jooqScope: JooqScope,
    private val mapper: AuthMapper,
) {

    suspend fun getByUserId(userId: UserID, providerType: ProviderType? = null): AuthEntity? =
        jooqScope.useDslContext { ctx ->
            ctx
                .selectFrom(AUTH_PROVIDER)
                .where(
                    AUTH_PROVIDER.USER_ID.eq(userId.value)
                        .andEqNotNull(AUTH_PROVIDER.PROVIDER_TYPE, providerType?.value)
                )
                .fetchOne()
                ?.toEntity()
        }

    suspend fun create(authEntity: AuthEntity) =
        jooqScope.useDslContext { ctx ->
            ctx
                .insertInto(AUTH_PROVIDER)
                .set(mapper.record(authEntity))
                .returning()
                .fetchOne()!!
                .toEntity()
        }

    suspend fun getAuthorityByEmail(email: String, type: ProviderType): AuthEntity? = jooqScope.useDslContext { ctx ->
        ctx
            .select(AUTH_PROVIDER)
            .from(
                AUTH_PROVIDER
                    .join(CLIENTS).on(AUTH_PROVIDER.USER_ID.eq(CLIENTS.ID))
            )
            .where(
                CLIENTS.EMAIL.eq(email)
                    .and(AUTH_PROVIDER.PROVIDER_TYPE.eq(type.value))
            )
            .fetchOne()
            ?.value1()
            ?.toEntity()
    }

    suspend fun getUserWithAuthInfo(email: String, type: ProviderType): UserWithAuthInfo? = jooqScope.useDslContext { ctx ->
        ctx
            .select(userAuthMapper)
            .from(
                CLIENTS
                    .join(AUTH_PROVIDER).on(AUTH_PROVIDER.USER_ID.eq(CLIENTS.ID))
            )
            .where(
                CLIENTS.EMAIL.eq(email)
                    .and(AUTH_PROVIDER.PROVIDER_TYPE.eq(type.value))
                )
            .fetchOne()
            ?.toUserWithAuthEntity()
    }
}