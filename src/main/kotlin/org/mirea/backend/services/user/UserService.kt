package org.mirea.backend.services.user

import org.mirea.backend.dto.user.UserDto
import org.mirea.backend.dto.user.toDto
import org.mirea.backend.entities.user.UserEntity
import org.mirea.backend.repositories.user.UserRepository
import org.mirea.backend.repositories.user.UserRepositorySearchQuery
import org.mirea.backend.utils.ids.UserID
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    suspend fun getCurrent(): UserDto {
        val email = SecurityContextHolder.getContext().authentication.name
        return getByEmail(email)!!.toDto()
    }

    suspend fun getById(id: UserID) = userRepository.getById(id)

    suspend fun getByEmail(email: String) = userRepository.getByEmail(email)

    suspend fun search(query: UserRepositorySearchQuery) = userRepository.search(query)

    suspend fun create(entity: UserEntity) = userRepository.create(entity)
}