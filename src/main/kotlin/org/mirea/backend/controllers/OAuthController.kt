package org.mirea.backend.controllers

import org.mirea.backend.dto.auth.AuthDto
import org.mirea.backend.services.auth.AuthService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/auth/oauth2")
class OAuthController(
    private val authService: AuthService
) {
    /**
     * Endpoint to get user information after successful OAuth2 login
     * This can be called by the frontend after the redirect
     */
    @GetMapping("/user")
    suspend fun getCurrentUser(principal: Principal): AuthDto {
        return if (principal is OAuth2AuthenticationToken) {
            authService.processOAuthLogin(principal)
        } else {
            throw IllegalStateException("Not authenticated with OAuth2")
        }
    }
} 