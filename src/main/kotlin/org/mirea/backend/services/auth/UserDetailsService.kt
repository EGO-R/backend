package org.mirea.backend.services.auth

import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.mirea.backend.entities.auth.AuthEntity
import org.mirea.backend.entities.auth.ProviderType
import org.mirea.backend.entities.user.UserEntity
import org.mirea.backend.repositories.auth.AuthRepository
import org.mirea.backend.repositories.user.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsService(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {
    private val logger = LogManager.getLogger()

    suspend fun loadUserByEmail(email: String): UserDetails {
        val user = runBlocking {
            userRepository.getByEmail(email) ?: throw UsernameNotFoundException("User not found")
        }
        logger.info("Retrieved user ${user.email}")
        val authInfo = runBlocking {
            authRepository.getByUserId(user.id, ProviderType.PASSWORD)
        } ?: throw UsernameNotFoundException("Password provider not found")

        return AppUserDetails(user, authInfo)
    }
}

class AppUserDetails(
    val user: UserEntity,
    private val authInfo: AuthEntity,
) : UserDetails {
    override fun getAuthorities() = listOf(user.role)
    override fun getPassword() = authInfo.credentials
    override fun getUsername() = user.email
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}
