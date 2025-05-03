package org.mirea.backend.controllers

import org.mirea.backend.dto.UserDto
import org.mirea.backend.services.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/current")
    suspend fun getCurrent(): UserDto = userService.getCurrent()
}