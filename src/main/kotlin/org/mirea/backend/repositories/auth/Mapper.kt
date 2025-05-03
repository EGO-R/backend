package org.mirea.backend.repositories.auth

import org.jooq.DSLContext
import org.mirea.backend.entities.auth.AuthEntity
import org.mirea.backend.jooq.generated.Tables.AUTH_PROVIDER
import org.mirea.backend.jooq.generated.tables.records.AuthProviderRecord
import org.mirea.backend.utils.ids.AuthID
import org.mirea.backend.utils.repositories.TableMapper
import org.springframework.stereotype.Component

@Component
class AuthMapper(
    dsl: DSLContext,
) : TableMapper<AuthEntity, AuthProviderRecord>(dsl) {
    override val updateIgnoreFields = setOf(AUTH_PROVIDER.USER_ID, AUTH_PROVIDER.PROVIDER_TYPE)

    override fun map(entity: AuthEntity) = fields {
        if (entity.id != AuthID.EMPTY) {
            AUTH_PROVIDER.ID set entity.id.value
        }
        AUTH_PROVIDER.USER_ID set entity.userId.value
        AUTH_PROVIDER.PROVIDER_TYPE set entity.providerType.value
        AUTH_PROVIDER.PROVIDER_USER_ID set entity.providerUserId
        AUTH_PROVIDER.CREDENTIALS set entity.credentials
    }
}
