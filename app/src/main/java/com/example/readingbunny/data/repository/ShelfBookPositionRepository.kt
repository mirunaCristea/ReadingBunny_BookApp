package com.example.readingbunny.data.repository

import com.example.readingbunny.data.local.ShelfBookPositionDao
import com.example.readingbunny.model.ShelfBookPosition
import kotlinx.coroutines.flow.Flow

class ShelfBookPositionRepository(
    private val positionDao: ShelfBookPositionDao
) {

    fun getAllPositions(): Flow<List<ShelfBookPosition>> =
        positionDao.getAllPositions()

    suspend fun savePosition(position: ShelfBookPosition) =
        positionDao.savePosition(position)

    suspend fun deletePosition(bookId: Int) =
        positionDao.deletePosition(bookId)
}