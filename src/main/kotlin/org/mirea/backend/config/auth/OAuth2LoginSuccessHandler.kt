package org.mirea.backend.config.auth

import kotlinx.coroutines.reactor.mono
import org.mirea.backend.entities.auth.AuthEntity
import org.mirea.backend.entities.auth.ProviderType
import org.mirea.backend.entities.user.UserEntity
import org.mirea.backend.entities.user.UserRole
import org.mirea.backend.repositories.auth.AuthRepository
import org.mirea.backend.services.DbTransactionManager
import org.mirea.backend.services.auth.JwtService
import org.mirea.backend.services.user.UserService
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.net.URI

@Component
class OAuth2LoginSuccessHandler(
    private val userService: UserService,
    private val authRepository: AuthRepository,
    private val jwtService: JwtService,
    private val transactionManager: DbTransactionManager
) : ServerAuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> = mono {
        val oAuth2User = authentication.principal as OAuth2User
        val oauthToken = authentication as OAuth2AuthenticationToken
        val provider = when (oauthToken.authorizedClientRegistrationId) {
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
            user = transactionManager.transactional {
                val newUser = userService.create(
                    UserEntity(
                        email = email,
                        displayName = name,
                        role = UserRole.USER
                    )
                )
                
                // Create auth provider entry
                authRepository.create(
                    AuthEntity(
                        userId = newUser.id,
                        providerType = provider,
                        providerUserId = providerId,
                        credentials = "" // Not needed for OAuth
                    )
                )
                
                newUser
            }
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
        val authDto = jwtService.generatePair(user)
        
        // Redirect to frontend with token
        val response = webFilterExchange.exchange.response
        redirectWithToken(response, authDto.token, authDto.refresh)
        
        null
    }
    
    private fun redirectWithToken(response: ServerHttpResponse, token: String, refreshToken: String) {
        // Redirect to your frontend application with the token
        // You'll need to adjust this URL to match your frontend
        val redirectUri = URI.create("http://localhost:3000/auth-callback?token=$token&refresh=$refreshToken")
        response.statusCode = org.springframework.http.HttpStatus.FOUND
        response.headers.location = redirectUri
    }
} 