package com.example.readingbunny.data.local

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.example.readingbunny.model.ShelfDecoration
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelfDecorationDao {

    @Insert
    suspend fun insertDecoration(
        decoration: ShelfDecoration
    )

    @Delete
    suspend fun deleteDecoration(
        decoration: ShelfDecoration
    )

    @Query("SELECT * FROM shelf_decorations ORDER BY id")
    fun getAllDecorations(): Flow<List<ShelfDecoration>>

    @Update
    suspend fun updateDecoration(decoration: ShelfDecoration)
}