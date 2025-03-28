package org.mirea.backend.repositories

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.mirea.backend.RepositoryTest
import org.mirea.backend.entities.UserEntity
import org.mirea.backend.repositories.user.UserRepository
import org.mirea.backend.utils.ids.UserID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql

@Sql(scripts = ["/sql/users/data.sql"])
class UserRepositoryTest : RepositoryTest() {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun getById() {
        val expected = UserEntity(
            id = UserID(1),
            name = "test1",
            email = "test1@mail.com",
        )
        val actual = runBlocking {
            userRepository.getById(UserID(1))
        }
        Assertions.assertThat(actual).isEqualTo(expected)
    }
}