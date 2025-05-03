package org.mirea.backend.services

import kotlinx.coroutines.reactor.awaitSingle
import org.mirea.backend.dto.auth.AuthRequest
import org.mirea.backend.dto.auth.AuthResponse
import org.mirea.backend.entities.auth.AuthEntity
import org.mirea.backend.entities.auth.ProviderType
import org.mirea.backend.entities.user.UserEntity
import org.mirea.backend.repositories.auth.AuthRepository
import org.mirea.backend.services.auth.JwtService
import org.mirea.backend.utils.ids.UserID
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
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
    suspend fun register(req: AuthRequest): AuthResponse = transactionManager.transactional {
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
        AuthResponse(
            token = jwtService.generate(user),
        )
    }

    suspend fun login(req: AuthRequest): AuthResponse {
        val auth = UsernamePasswordAuthenticationToken(req.email, req.password)
        authManager.authenticate(auth).awaitSingle()
        val user = userService.getByEmail(req.email)!!

        val token = jwtService.generate(user)
        return AuthResponse(token)
    }

    suspend fun getByUserId(userId: UserID) = authRepository.getByUserId(userId)
}