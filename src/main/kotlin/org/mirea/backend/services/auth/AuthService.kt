package org.mirea.backend.services.auth

import kotlinx.coroutines.reactor.awaitSingle
import org.mirea.backend.dto.auth.AuthDto
import org.mirea.backend.dto.auth.AuthRequest
import org.mirea.backend.dto.auth.AuthResponse
import org.mirea.backend.dto.user.toDto
import org.mirea.backend.entities.auth.AuthEntity
import org.mirea.backend.entities.auth.ProviderType
import org.mirea.backend.entities.user.UserEntity
import org.mirea.backend.exceptions.UserNotFoundException
import org.mirea.backend.repositories.auth.AuthRepository
import org.mirea.backend.services.DbTransactionManager
import org.mirea.backend.services.user.UserService
import org.mirea.backend.utils.ids.UserID
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authManager: ReactiveAuthenticationManager,
    private val passwordEncoder: PasswordEncoder,
    private val userService: UserService,
    private val authRepository: AuthRepository,
    private val transactionManager: DbTransactionManager,
    private val jwtService: JwtService,
) {
    suspend fun register(req: AuthRequest): AuthDto = transactionManager.transactional {
        val user = userService.create(
            UserEntity(
                email = req.email,
                displayName = req.email.substringBefore("@"),
            )
        )

        val credentials = passwordEncoder.encode(req.password)
        val authProvider = AuthEntity(
            providerType = ProviderType.PASSWORD,
            credentials = credentials,
            userId = user.id,
            providerUserId = null,
        )
        authRepository.create(authProvider)
        jwtService.generatePair(user)
    }

    suspend fun login(req: AuthRequest): AuthDto {
        val auth = UsernamePasswordAuthenticationToken(req.email, req.password)
        authManager.authenticate(auth).awaitSingle()
        val user = userService.getByEmail(req.email) ?: throw UserNotFoundException()

        return jwtService.generatePair(user)
    }

    suspend fun rotate(refreshRaw: String) = jwtService.rotate(refreshRaw)

    suspend fun getByUserId(userId: UserID) = authRepository.getByUserId(userId)
    
    suspend fun processOAuthLogin(authentication: OAuth2AuthenticationToken): AuthDto = transactionManager.transactional {
        val oAuth2User = authentication.principal
        val provider = when (authentication.authorizedClientRegistrationId) {
            "google" -> ProviderType.GOOGLE
            else -> throw IllegalArgumentException("Unsupported OAuth provider")
        }
        
        val email = oAuth2User.attributes["email"] as String
        val name = oAuth2User.attributes["name"] as String
        val providerId = (oAuth2User.attributes["sub"] as String).toLongOrNull()
        
        // Check if user exists with this email
        var user = userService.getByEmail(email)
        
        if (user == null) {
            // Create new user
            user = userService.create(
                UserEntity(
                    email = email,
                    displayName = name,
                )
            )
            
            // Create auth provider entry
            authRepository.create(
                AuthEntity(
                    userId = user.id,
                    providerType = provider,
                    providerUserId = providerId,
                    credentials = "" // Not needed for OAuth
                )
            )
        } else {
            // Check if the provider is already connected, if not, connect it
            val existingAuth = authRepository.getByUserId(user.id, provider)
            if (existingAuth == null) {
                authRepository.create(
                    AuthEntity(
                        userId = user.id,
                        providerType = provider,
                        providerUserId = providerId,
                        credentials = "" // Not needed for OAuth
                    )
                )
            }
        }
        
        // Generate JWT tokens
        jwtService.generatePair(user)
    }
}