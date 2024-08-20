package com.example.coursework2

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [League::class, Club::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getLeagueDao(): LeagueDao
    abstract fun getClubDao(): ClubDao
}