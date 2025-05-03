package org.mirea.backend.controllers

import org.mirea.backend.dto.auth.AuthRequest
import org.mirea.backend.dto.auth.AuthResponse
import org.mirea.backend.services.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/register")
    suspend fun register(@RequestBody req: AuthRequest): AuthResponse = authService.register(req)

    @PostMapping("/login")
    suspend fun login(@RequestBody req: AuthRequest): AuthResponse = authService.login(req)
}
