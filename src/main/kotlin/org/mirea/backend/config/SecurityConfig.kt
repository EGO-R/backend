package org.mirea.backend.config

import io.jsonwebtoken.security.Keys
import org.mirea.backend.config.auth.OAuth2LoginSuccessHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import reactor.core.publisher.Mono


@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
class SecurityConfig {
    @Bean
    fun chain(
        http: ServerHttpSecurity,
        cors: CorsConfigurationSource,
        converter: Converter<Jwt, Mono<AbstractAuthenticationToken>>,
        oAuth2LoginSuccessHandler: OAuth2LoginSuccessHandler
    ): SecurityWebFilterChain = http {
        cors { configurationSource = cors }
        csrf { disable() }
        authorizeExchange {
            authorize(
                pathMatchers(HttpMethod.OPTIONS, "/**"),
                permitAll,
            )
            authorize("/api/auth/**", permitAll)
            authorize("/login/oauth2/code/**", permitAll)
            authorize(anyExchange, authenticated)
        }

        oauth2ResourceServer {
            jwt { jwtAuthenticationConverter = converter }
        }
        
        oauth2Login {
            authenticationSuccessHandler = oAuth2LoginSuccessHandler
        }
    }


    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource =
        UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", CorsConfiguration().apply {
                allowedOrigins = listOf("http://localhost:3000")
                allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                allowedHeaders = listOf("*")
                allowCredentials = true
                maxAge = 3600
            })
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

