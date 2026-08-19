package com.example.readingbunny.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.example.readingbunny.model.ShelfBookPosition
import kotlinx.coroutines.flow.Flow

@Dao
interface ShelfBookPositionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePosition(position: ShelfBookPosition)

    @Query("SELECT * FROM shelf_book_positions")
    fun getAllPositions(): Flow<List<ShelfBookPosition>>

    @Query("DELETE FROM shelf_book_positions WHERE bookId = :bookId")
    suspend fun deletePosition(bookId: Int)

    @Query("DELETE FROM shelf_book_positions")
    suspend fun deleteAllPositions()

}
