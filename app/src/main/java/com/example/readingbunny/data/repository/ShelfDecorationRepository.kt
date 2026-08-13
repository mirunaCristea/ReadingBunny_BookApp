package com.example.readingbunny.data.repository

import com.example.readingbunny.data.local.ShelfDecorationDao
import com.example.readingbunny.model.ShelfDecoration
import kotlinx.coroutines.flow.Flow

class ShelfDecorationRepository(
    private val decorationDao: ShelfDecorationDao
) {

    fun getAllDecorations(): Flow<List<ShelfDecoration>> =
        decorationDao.getAllDecorations()

    suspend fun addDecoration(decoration: ShelfDecoration) =
        decorationDao.insertDecoration(decoration)

    suspend fun deleteDecoration(decoration: ShelfDecoration) =
        decorationDao.deleteDecoration(decoration)

    suspend fun updateDecoration(decoration: ShelfDecoration) =
        decorationDao.updateDecoration(decoration)
}