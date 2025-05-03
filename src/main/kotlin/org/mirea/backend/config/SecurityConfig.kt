package org.mirea.backend.config

import io.jsonwebtoken.security.Keys
import kotlinx.coroutines.reactor.mono
import org.mirea.backend.services.AuthService
import org.mirea.backend.services.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono


@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
class SecurityConfig {
    @Bean
    fun chain(
        http: ServerHttpSecurity,
        converter: Converter<Jwt, Mono<AbstractAuthenticationToken>>
    ): SecurityWebFilterChain = http {
        csrf { disable() }
        cors { }

        authorizeExchange {
            authorize("/api/auth/**", permitAll)
            authorize(anyExchange, authenticated)
        }

        oauth2ResourceServer {
            jwt { jwtAuthenticationConverter = converter }
        }
    }

    @Bean
    fun jwtDecoder(@Value("\${jwt.secret}") secret: String): ReactiveJwtDecoder =
        NimbusReactiveJwtDecoder
            .withSecretKey(Keys.hmacShaKeyFor(secret.toByteArray()))
            .macAlgorithm(MacAlgorithm.HS256)
            .build()

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}

