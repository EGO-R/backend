package org.mirea.backend.repositories.user

import org.jooq.DSLContext
import org.jooq.TableField
import org.mirea.backend.entities.user.UserEntity
import org.mirea.backend.jooq.generated.Tables.CLIENTS
import org.mirea.backend.jooq.generated.tables.records.ClientsRecord
import org.mirea.backend.utils.ids.UserID
import org.mirea.backend.utils.repositories.TableMapper
import org.springframework.stereotype.Component

@Component
class UserMapper(
    dsl: DSLContext,
) : TableMapper<UserEntity, ClientsRecord>(dsl) {
    override val updateIgnoreFields: Set<TableField<ClientsRecord, *>> = emptySet()

    override fun map(entity: UserEntity): Map<TableField<ClientsRecord, *>, *> = fields {
        if (entity.id != UserID.EMPTY) {
            CLIENTS.ID set entity.id.value
        }
        CLIENTS.NAME set entity.displayName
        CLIENTS.EMAIL set entity.email
        CLIENTS.ROLE set entity.role.value
    }
}
