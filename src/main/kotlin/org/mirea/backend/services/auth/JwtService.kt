package org.mirea.backend.services.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.mirea.backend.entities.user.UserEntity
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.time.Duration.Companion.minutes

@Service
class JwtService(
    @Value("\${jwt.secret}") secret: String,
    private val clock: Clock = Clock.systemUTC()
) {
    private val key = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generate(user: UserEntity, ttl: Duration = Duration.of(15, ChronoUnit.MINUTES)): String =
        Jwts.builder()
            .subject(user.email)
            .claims(
                mapOf(
                    "id" to user.id,
                    "role" to user.role,
                ),
            )
            .issuedAt(Date.from(clock.instant()))
            .expiration(Date.from(clock.instant().plus(ttl)))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
}
