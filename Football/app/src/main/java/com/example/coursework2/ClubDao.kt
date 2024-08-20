package com.example.coursework2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ClubDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clubs: MutableList<Club>)

    @Query("select * from Club where strTeam LIKE :strSearch or strLeague LIKE :strSearch")
    suspend fun searchClubs(strSearch: String): List<Club>
}