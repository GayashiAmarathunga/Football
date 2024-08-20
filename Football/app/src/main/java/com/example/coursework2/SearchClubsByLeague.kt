package com.example.coursework2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

lateinit var clubDao: ClubDao
val clubs = mutableListOf<Club>()

class SearchClubsByLeague : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clubDao = db.getClubDao()

        setContent {
            SearchByLeague()
        }
    }
}

@Composable
fun SearchByLeague() {
    // the league name keyword to search for
    var keyword by rememberSaveable { mutableStateOf("") }
    // Creates a CoroutineScope bound to the GUI composable lifecycle
    val scope = rememberCoroutineScope()
    var clubInfoDisplay by rememberSaveable { mutableStateOf(" ") }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Search Clubs By League",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF304D30)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            TextField(
                value = keyword,
                onValueChange = { keyword = it },
                label = { Text("Search") },
                colors = TextFieldDefaults.colors(
                    cursorColor = Color(0xFF304D30),
                    focusedIndicatorColor = Color(0xFF304D30),
                    unfocusedIndicatorColor = Color(0xFF304D30),
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            Row {
                Button(
                    onClick = {
                        scope.launch {
                            clubInfoDisplay = fetchClubs(keyword)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Retrieve Clubs")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = {
                        scope.launch {
                            clubDao.insertAll(clubs)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Save clubs to Database")
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            Text(
                text = clubInfoDisplay,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

suspend fun fetchClubs(keyword: String): String {
//val urlString = "https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=English%20Premier%20League"
    val urlString =
        "https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=${keyword}"
    val url = URL(urlString)
    val con: HttpURLConnection = withContext(Dispatchers.IO) {
        url.openConnection()
    } as HttpURLConnection
// collecting all the JSON string
    val stb = StringBuilder()
// run the code of the launched coroutine in a new thread
    withContext(Dispatchers.IO) {
        val bf = BufferedReader(InputStreamReader(con.inputStream))
        var line: String? = bf.readLine()
        while (line != null) { // keep reading until no more lines of text
            stb.append(line + "\n")
            line = bf.readLine()
        }
    }
    return parseJSON(stb)
}

fun parseJSON(stb: StringBuilder): String {
    // this contains the full JSON returned by the Web Service
    val json = JSONObject(stb.toString())
    // Information about all the clubs extracted by this function
    val allClubs = StringBuilder()
    if (!json.isNull("teams")) {
        val jsonArray: JSONArray = json.getJSONArray("teams")
        // extract all the clubs from the JSON array
        for (i in 0..<jsonArray.length()) {
            val clubJson: JSONObject = jsonArray.getJSONObject(i) // this is a json object
            // extract the team id
            val teamId = clubJson.optInt("idTeam", 0)
            allClubs.append("\"idTeam\" : \"$teamId\" ,\n")
            // extract the team name
            val teamName = clubJson.optString("strTeam", "")
            allClubs.append("\"Name\" : \"$teamName\" ,\n")
            // extract the team name short
            val strTeamShort = clubJson.optString("strTeamShort", "")
            allClubs.append("\"strTeamShort\" : \"$strTeamShort\" ,\n")
            // extract the team name Alternate
            val strAlternate = clubJson.optString("strAlternate", "")
            allClubs.append("\"strAlternate\" : \"$strAlternate\" ,\n")
            // extract the team FormedYear
            val intFormedYear = clubJson.optInt("intFormedYear", 0)
            allClubs.append("\"intFormedYear\" : \"$intFormedYear\" ,\n")
            // extract the team League
            val strLeague = clubJson.optString("strLeague", "")
            allClubs.append("\"strLeague\" : \"$strLeague\" ,\n")
            // extract the idLeague
            val idLeague = clubJson.optInt("idLeague", 0)
            allClubs.append("\"idLeague\" : \"$idLeague\" ,\n")
            // extract the strStadium
            val strStadium = clubJson.optString("strStadium", "")
            allClubs.append("\"strStadium\" : \"$strStadium\" ,\n")
            // extract the strKeywords
            val strKeywords = clubJson.optString("strKeywords", "")
            allClubs.append("\"strKeywords\" : \"$strKeywords\" ,\n")
            // extract the strStadiumThumb
            val strStadiumThumb = clubJson.optString("strStadiumThumb", "")
            allClubs.append("\"strStadiumThumb\" : \"$strStadiumThumb\" ,\n")
            // extract the strStadiumLocation
            val strStadiumLocation = clubJson.optString("strStadiumLocation", "")
            allClubs.append("\"strStadiumLocation\" : \"$strStadiumLocation\" ,\n")
            // extract the intStadiumCapacity
            val intStadiumCapacity = clubJson.optInt("intStadiumCapacity", 0)
            allClubs.append("\"intStadiumCapacity\" : \"$intStadiumCapacity\" ,\n")
            // extract the strWebsite
            val strWebsite = clubJson.optString("strWebsite", "")
            allClubs.append("\"strWebsite\" : \"$strWebsite\" ,\n")
            // extract the strTeamJersey
            val strTeamJersey = clubJson.optString("strTeamJersey", "")
            allClubs.append("\"strTeamJersey\" : \"$strTeamJersey\" ,\n")
            // extract the strTeamLogo
            val strTeamLogo = clubJson.optString("strTeamLogo", "")
            allClubs.append("\"strTeamLogo\" : \"$strTeamLogo\" ,\n")
            val club = Club(
                idTeam = teamId,
                strTeam = teamName,
                strTeamShort = strTeamShort,
                strAlternate = strAlternate,
                intFormedYear = intFormedYear,
                strLeague = strLeague,
                idLeague = idLeague,
                strStadium = strStadium,
                strKeywords = strKeywords,
                strStadiumThumb = strStadiumThumb,
                strStadiumLocation = strStadiumLocation,
                intStadiumCapacity = intStadiumCapacity,
                strWebsite = strWebsite,
                strTeamJersey = strTeamJersey,
                strTeamLogo = strTeamLogo
            )
            clubs.add(club)
            allClubs.append("\n\n")
        }
    } else {
        allClubs.append("No teams found to the given keyword")
    }

    return allClubs.toString()
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchClubs() {
    SearchByLeague()
}