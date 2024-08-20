package com.example.coursework2

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coursework2.ui.theme.Coursework2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class ViewJerseys : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ViewJerseysByClub()
        }
    }
}


@Composable
fun ViewJerseysByClub(){
    // the club name keyword to search for
    var keyword by rememberSaveable { mutableStateOf("") }
    // Creates a CoroutineScope bound to the GUI composable lifecycle
    val scope = rememberCoroutineScope()
    var teamName by rememberSaveable { mutableStateOf(" ") }
    var jerseys by rememberSaveable { mutableStateOf(listOf<String>()) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "View Jerseys by Club",
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
            Button(
                onClick = {
                    scope.launch {
                        val result = fetchClubsName(keyword)
                        teamName = result.first
                        jerseys = result.second
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("View")
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            Text(
                text = teamName,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
        items(jerseys) {jerseyUrl ->
            val imageBitmap = loadImage(jerseyUrl).value
            imageBitmap?.let {
                Image(bitmap = it , contentDescription = "Jersey Image", modifier = Modifier.fillMaxSize())
            }
        }
    }
}

suspend fun fetchClubsName(keyword: String): Pair<String, List<String>> {
//val urlString = "https://www.thesportsdb.com/api/v1/json/3/searchteams.php?t=Arsenal"
    val urlString =
        "https://www.thesportsdb.com/api/v1/json/3/searchteams.php?t=$keyword"
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
    return parseJSONClubs(stb, keyword)
}

suspend fun parseJSONClubs(stb: StringBuilder, keyword: String): Pair<String, List<String>> {
    // this contains the full JSON returned by the Web Service
    val json = JSONObject(stb.toString())
    val teamNames = StringBuilder()
    val jerseys = mutableListOf<String>()
    if (!json.isNull("teams")) {
        val jsonArray: JSONArray = json.getJSONArray("teams")
        // extract all the clubs from the JSON array
        for (i in 0..<jsonArray.length()) {
            val clubJson: JSONObject = jsonArray.getJSONObject(i) // this is a json object
            // extract the team id
            val teamId = clubJson.optInt("idTeam", 0)
            // extract the team name
            val teamName = clubJson.optString("strTeam", "")
            //Check if the name contains the keyword
            if (teamName.contains(keyword, ignoreCase = true)){
                teamNames.append(teamName)
                //add jerseys
                jerseys.addAll(fetchJerseys(teamId))
            }
        }
    } else {
        teamNames.append("No teams found to the given keyword")
    }

    return Pair(teamNames.toString(), jerseys)
}

suspend fun fetchJerseys(teamId: Int): List<String> {
//val urlString = "https://www.thesportsdb.com/api/v1/json/3/lookupequipment.php?id=133597"
    val urlString =
        "https://www.thesportsdb.com/api/v1/json/3/lookupequipment.php?id=$teamId"
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
    return parseJSONJerseys(stb)
}

fun parseJSONJerseys(stb: StringBuilder): List<String> {
    val jerseys = mutableListOf<String>()
    // this contains the full JSON returned by the Web Service
    val json = JSONObject(stb.toString())
    if (!json.isNull("equipment")) {
        val jsonArray: JSONArray = json.getJSONArray("equipment")
        // extract all the clubs from the JSON array
        for (i in 0..<jsonArray.length()) {
            val jerseyJson: JSONObject = jsonArray.getJSONObject(i) // this is a json object
            // extract the jersey
            val jersey = jerseyJson.optString("strEquipment", "")
            jerseys.add(jersey)
        }
    }

    return jerseys
}

@Preview(showBackground = true)
@Composable
fun PreviewViewJerseys() {
    ViewJerseysByClub()
}