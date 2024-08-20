package com.example.coursework2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class League(
    @PrimaryKey var idLeague: Int,
    var strLeague: String,
    var strSport: String,
    var strLeagueAlternate: String
)