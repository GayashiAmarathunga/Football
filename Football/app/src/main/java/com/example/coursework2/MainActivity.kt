//Gayashi Amarathunga

//  https://drive.google.com/file/d/1OVxlcGjNsOKYAnt8iwZVkeGLtear7xl3/view?usp=sharing
package com.example.coursework2

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.room.Room
import kotlinx.coroutines.launch

// Declaring global variables for the database and DAO
lateinit var db: AppDatabase
lateinit var leagueDao: LeagueDao

// Main activity class
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Building Room database instance
        db = Room.databaseBuilder(this,
            AppDatabase::class.java, "mydatabase").build()
        leagueDao = db.getLeagueDao()

        setContent {
            HomeScreen()
        }
    }
}

@Composable
// Composable function for the home screen UI
fun HomeScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Column layout for arranging UI elements vertically
    Column {
        //Display the background image
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    }
    // LazyColumn for displaying a scrollable list of UI elements
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Item for displaying the title
        item {
            Text(
                text = "Football Clubs",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.height(35.dp))
        }
        // Item for adding leagues
        val buttonModifier = Modifier
            .padding(8.dp)
            .width(220.dp)
            .height(45.dp)
        item {
            Button(
                onClick = {
                    scope.launch {
                        leagueDao.insertAll(
                            listOf(
                                League(4328, "English Premier League","Soccer","Premier League, EPL"),
                                League(4329, "English League Championship","Soccer","Championship"),
                                League(4330, "Scottish Premier League","Soccer","Scottish Premiership, SPFL"),
                                League(4331, "German Bundesliga","Soccer","Bundesliga, Fußball-Bundesliga"),
                                League(4332, "Italian Serie A","Soccer","Serie A"),
                                League(4334, "French Ligue 1","Soccer","Ligue 1 Conforama"),
                                League(4335, "Spanish La Liga","Soccer","LaLiga Santander, La Liga"),
                                League(4336, "Greek Superleague Greece","Soccer",""),
                                League(4337, "Dutch Eredivisie","Soccer","Eredivisie"),
                                League(4338, "Belgian First Division A","Soccer","Jupiler Pro League"),
                                League(4339, "Turkish Super Lig","Soccer","Super Lig"),
                                League(4340, "Danish Superliga","Soccer",""),
                                League(4344, "Portuguese Primeira Liga","Soccer","Liga NOS"),
                                League(4346, "American Major League Soccer","Soccer","MLS, Major League Soccer"),
                                League(4347, "Swedish Allsvenskan","Soccer","Fotbollsallsvenskan"),
                                League(4350, "Mexican Primera League","Soccer","Liga MX"),
                                League(4351, "Brazilian Serie A","Soccer",""),
                                League(4354, "Ukrainian Premier League","Soccer",""),
                                League(4355, "Russian Football Premier League","Soccer","Чемпионат России по футболу"),
                                League(4356, "Australian A-League","Soccer","A-League"),
                                League(4358, "Norwegian Eliteserien","Soccer","Eliteserien"),
                                League(4359, "Chinese Super League","Soccer","")
                            )
                        )
                    }
                },
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = "Add Leagues",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        // Item for searching clubs by league
        item {
            Button(
                onClick = {
                    val intent =
                        Intent(context, SearchClubsByLeague::class.java)
                    context.startActivity(intent)
                },
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = "Search for Clubs By League",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        // Item for searching clubs
        item {
            Button(
                onClick = {
                    val intent = Intent(context, SearchClubs::class.java)
                    context.startActivity(intent)
                },
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = "Search for Clubs",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        // Item for viewing jerseys
        item {
            Button(
                onClick = {
                    val intent = Intent(context, ViewJerseys::class.java)
                    context.startActivity(intent)
                },
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = "View Jerseys",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}