package org.mirea.backend.controllers

import com.nimbusds.oauth2.sdk.TokenResponse
import org.mirea.backend.dto.auth.AuthDto
import org.mirea.backend.dto.auth.AuthRequest
import org.mirea.backend.dto.auth.AuthResponse
import org.mirea.backend.services.auth.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/register")
    suspend fun register(@RequestBody req: AuthRequest,
                         response: ServerHttpResponse): AuthResponse {
        val authDto = authService.register(req)
        response.setRefreshCookie(authDto)
        return AuthResponse(
            token = authDto.token,
            user = authDto.user,
        )
    }

    @PostMapping("/login")
    suspend fun login(@RequestBody req: AuthRequest,
                      response: ServerHttpResponse): AuthResponse {
        val authDto = authService.login(req)
        response.setRefreshCookie(authDto)
        return AuthResponse(
            token = authDto.token,
            user = authDto.user,
        )
    }

    @PostMapping("/refresh")
    suspend fun refresh(@CookieValue("refresh_token") refreshRaw: String?, response: ServerHttpResponse): AuthResponse {
        if (refreshRaw == null) throw ResponseStatusException(HttpStatus.UNAUTHORIZED)

        val authDto = authService.rotate(refreshRaw)
        response.setRefreshCookie(authDto)
        return AuthResponse(
            token = authDto.token,
            user = authDto.user,
        )
    }

    private fun ServerHttpResponse.setRefreshCookie(authDto: AuthDto) {
        addCookie(
            ResponseCookie.from("refresh_token", authDto.refresh)
                .httpOnly(true).secure(true)
                .sameSite("Lax").path("/api/auth/refresh")
                .maxAge(authDto.age)
                .build()
        )
    }
}
