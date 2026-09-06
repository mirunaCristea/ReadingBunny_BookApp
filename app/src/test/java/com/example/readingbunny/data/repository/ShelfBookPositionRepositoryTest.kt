package com.example.readingbunny.data.repository

import com.example.readingbunny.data.local.ShelfBookPositionDao
import com.example.readingbunny.model.ShelfBookPosition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ShelfBookPositionRepositoryTest {

    @Test
    fun savePosition_savesBookPosition() =
        runBlocking {

            val dao =
                FakeShelfBookPositionDao()

            val repository =
                ShelfBookPositionRepository(dao)

            val position =
                ShelfBookPosition(
                    bookId = 1,
                    shelfIndex = 2,
                    slotIndex = 3
                )

            repository.savePosition(position)

            val positions =
                repository
                    .getAllPositions()
                    .first()

            assertEquals(1, positions.size)

            assertEquals(
                position,
                positions.first()
            )
        }

    @Test
    fun savePosition_replacesOldPositionForSameBook() =
        runBlocking {

            val dao =
                FakeShelfBookPositionDao()

            val repository =
                ShelfBookPositionRepository(dao)

            repository.savePosition(
                ShelfBookPosition(
                    bookId = 1,
                    shelfIndex = 0,
                    slotIndex = 0
                )
            )

            repository.savePosition(
                ShelfBookPosition(
                    bookId = 1,
                    shelfIndex = 2,
                    slotIndex = 4
                )
            )

            val positions =
                repository
                    .getAllPositions()
                    .first()

            assertEquals(1, positions.size)

            assertEquals(
                2,
                positions.first().shelfIndex
            )

            assertEquals(
                4,
                positions.first().slotIndex
            )
        }

    @Test
    fun deletePosition_removesBookPosition() =
        runBlocking {

            val dao =
                FakeShelfBookPositionDao()

            val repository =
                ShelfBookPositionRepository(dao)

            repository.savePosition(
                ShelfBookPosition(
                    bookId = 1,
                    shelfIndex = 0,
                    slotIndex = 1
                )
            )

            repository.deletePosition(1)

            assertTrue(
                repository
                    .getAllPositions()
                    .first()
                    .isEmpty()
            )
        }

    private class FakeShelfBookPositionDao :
        ShelfBookPositionDao {

        private val positions =
            MutableStateFlow<
                    List<ShelfBookPosition>
                    >(emptyList())

        override suspend fun savePosition(
            position: ShelfBookPosition
        ) {

            positions.value =
                positions.value
                    .filterNot {
                        it.bookId ==
                                position.bookId
                    } + position
        }

        override fun getAllPositions():
                Flow<List<ShelfBookPosition>> {

            return positions
        }

        override suspend fun deletePosition(
            bookId: Int
        ) {

            positions.value =
                positions.value.filterNot {
                    it.bookId == bookId
                }
        }

        override suspend fun deleteAllPositions() {

            positions.value =
                emptyList()
        }
    }
}