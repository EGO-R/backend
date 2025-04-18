package org.mirea.backend.services

import org.mirea.backend.repositories.user.UserRepository
import org.mirea.backend.repositories.user.UserRepositorySearchQuery
import org.mirea.backend.utils.ids.UserID
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    suspend fun getById(id: UserID) = userRepository.getById(id)

    suspend fun search(query: UserRepositorySearchQuery) = userRepository.search(query)
}