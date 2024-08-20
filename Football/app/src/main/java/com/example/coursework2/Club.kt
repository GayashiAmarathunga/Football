package com.example.coursework2

import androidx.room.Entity
import androidx.room.PrimaryKey
// Declaring an entity class representing a Club
@Entity
data class Club (
    // Declaring primary key for the entity
    @PrimaryKey var idTeam: Int,
    // Declaring properties for various attributes of a club
    var strTeam: String,
    var strTeamShort: String,
    var strAlternate:String,
    var intFormedYear: Int,
    var strLeague: String,
    var idLeague: Int,
    var strStadium: String,
    var strKeywords: String,
    var strStadiumThumb: String,
    var strStadiumLocation: String,
    var intStadiumCapacity: Int,
    var strWebsite: String,
    var strTeamJersey: String,
    var strTeamLogo: String
)