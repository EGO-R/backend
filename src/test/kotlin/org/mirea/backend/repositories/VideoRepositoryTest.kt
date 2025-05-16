package org.mirea.backend.repositories

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mirea.backend.RepositoryTest
import org.mirea.backend.entities.video.VideoEntity
import org.mirea.backend.repositories.video.VideoRepository
import org.mirea.backend.repositories.video.queries.VideoIdPaginationData
import org.mirea.backend.repositories.video.queries.VideoRepositorySearchQuery
import org.mirea.backend.utils.ids.UserID
import org.mirea.backend.utils.ids.VideoID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql

@ActiveProfiles("test")
@Sql(scripts = ["/sql/users/data.sql", "/sql/videos/data.sql"])
class VideoRepositoryTest : RepositoryTest() {
    @Autowired
    private lateinit var videoRepository: VideoRepository

    companion object {
        const val NEXT_INSERT_ID = 3L
    }

    @Nested
    inner class Search {
        @Test
        fun search() {
            val query = VideoRepositorySearchQuery.create {
                name = "test"
                userID = UserID(1)
            }

            val expected = listOf(
                VideoEntity(
                    id = VideoID(1),
                    name = "test1",
                    userId = UserID(1),
                    videoUrl = "1",
                    preview = "1"
                ),
            )

            val actual = runBlocking {
                videoRepository.search(query)
            }

            Assertions.assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun pagination() {
            val paginationData = VideoIdPaginationData.create(lastSelectedID = VideoID(1)) {
                size = 2
            }

            val query = VideoRepositorySearchQuery.create {
                this.paginationData = paginationData
            }

            val expected = listOf(
                VideoEntity(
                    id = VideoID(2),
                    name = "test2",
                    userId = UserID(2),
                    preview = "2",
                    videoUrl = "2",
                ),
            )

            val actual = runBlocking {
                videoRepository.search(query)
            }

            Assertions.assertThat(actual).isEqualTo(expected)
        }

        @Test
        fun getById() {
            val searchId = VideoID(2)

            val expected = VideoEntity(
                id = VideoID(2),
                name = "test2",
                userId = UserID(2),
                preview = "2",
                videoUrl = "2",
            )

            val actual = runBlocking {
                videoRepository.getById(searchId)
            }

            Assertions.assertThat(actual).isEqualTo(expected)
        }
    }

    @Nested
    inner class Create {
        @Test
        fun create() {
            val entity = VideoEntity(
                id = VideoID.EMPTY,
                name = "test3",
                userId = UserID(2),
                preview = "2",
                videoUrl = "2",
            )

            val expected = VideoEntity(
                id = VideoID(NEXT_INSERT_ID),
                name = "test3",
                userId = UserID(2),
                preview = "2",
                videoUrl = "2",
            )

            val actual = runBlocking {
                videoRepository.upsert(entity)
            }

            Assertions.assertThat(actual).isEqualTo(expected)
        }
    }

    @Nested
    inner class Update {
        @Test
        fun update() {
            val entity = VideoEntity(
                id = VideoID(2),
                name = "test3",
                userId = UserID(3),
                preview = "2",
                videoUrl = "2",
            )

            val expected = VideoEntity(
                id = VideoID(2),
                name = "test3",
                userId = UserID(2),
                preview = "2",
                videoUrl = "2",
            )

            val actual = runBlocking {
                videoRepository.upsert(entity)
            }

            Assertions.assertThat(actual).isEqualTo(expected)
        }
    }

    @Nested
    inner class Delete {
        @Test
        fun delete() {
            val query = VideoRepositorySearchQuery.create {
                id = VideoID(2)
            }

            val expected = listOf(
                VideoEntity(
                    id = VideoID(1),
                    name = "test1",
                    userId = UserID(1),
                    preview = "1",
                    videoUrl = "1",
                ),
            )

            val expectedDeletedIds = listOf(VideoID(2))

            val actualDeletedIds = runBlocking {
                videoRepository.delete(query)
            }

            val actual = runBlocking {
                videoRepository.search(VideoRepositorySearchQuery.create {})
            }

            Assertions.assertThat(actualDeletedIds).isEqualTo(expectedDeletedIds)
            Assertions.assertThat(actual).isEqualTo(expected)
        }
    }
}