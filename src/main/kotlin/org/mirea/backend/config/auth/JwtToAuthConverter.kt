package org.mirea.backend.config.auth

import kotlinx.coroutines.reactor.mono
import org.mirea.backend.services.user.UserService
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtToAuthConverter(
    private val userService: UserService,
) : Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    override fun convert(jwt: Jwt): Mono<AbstractAuthenticationToken> = mono {
        val user = userService.getByEmail(jwt.subject)
            ?: throw UsernameNotFoundException("User not found")

        UsernamePasswordAuthenticationToken(
            user,
            jwt,
            listOf(user.role),
        )
    }
}
