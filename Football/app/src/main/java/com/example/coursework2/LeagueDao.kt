package com.example.coursework2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LeagueDao {
    @Query("select * from league")
    suspend fun getAll(): List<League>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(leagues: List<League>)
}