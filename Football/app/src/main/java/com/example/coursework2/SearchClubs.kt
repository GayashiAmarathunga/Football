package com.example.coursework2


import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class SearchClubs : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clubDao = db.getClubDao()
        setContent {
            Search()
        }
    }
}

@Composable
fun Search() {
    var keyword by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var clubResults by rememberSaveable { mutableStateOf(listOf<Club>()) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Search Clubs",
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
                        clubResults =
                            clubDao.searchClubs("%$keyword%") //% to match any sequence of string
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(text = "Search")
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
        items(clubResults) { club ->
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = club.strTeam,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )
                val imageBitmap = loadImage(club.strTeamLogo).value
                imageBitmap?.let {
                    Image(bitmap = it , contentDescription = "Team Logo", modifier = Modifier.fillMaxSize())
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "idTeam:${club.idTeam}\nName:${club.strTeam} \nstrTeamShort:${club.strTeamShort}\nstrAlternate:${club.strAlternate}\nintFormedYear:${club.intFormedYear}\nstrLeague:${club.strLeague}\nidLeague:${club.idLeague}\nstrStadium:${club.strStadium}\nstrKeywords:${club.strKeywords}\nstrStadiumThumb:${club.strStadiumThumb}\nstrStadiumLocation:${club.strStadiumLocation}\nintStadiumCapacity:${club.intStadiumCapacity}\nstrWebsite:${club.strWebsite}\nstrTeamJersey:${club.strTeamJersey}\nstrTeamLogo:${club.strTeamLogo}\n\n\n ",
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
    }
}

@Composable
fun loadImage(url: String): State<ImageBitmap?> {
    val imageBitmap = remember { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(url) {
        val bitmap = withContext(Dispatchers.IO){
            var connection: HttpURLConnection? = null
            try {
                //creating a URL object from the image url
                val imageUrl = URL(url)
                //open a connection to the url using HttpURLConnection
                connection = imageUrl.openConnection() as HttpURLConnection
                connection.connect()
                //check if the response code is OK
                if (connection.responseCode == HttpURLConnection.HTTP_OK){
                    //read the input and decode it to bitmap using BitmapFactory
                    BufferedInputStream(connection.inputStream).use {
                        BitmapFactory.decodeStream(it)?.asImageBitmap()
                    }
                } else{
                    null
                }
            } catch (e: IOException){
                e.printStackTrace()
            } finally {
                connection?.disconnect()
            }
        }
        imageBitmap.value = bitmap as ImageBitmap?
    }
    return imageBitmap
}

@Preview(showBackground = true)
@Composable
fun PreviewSearch() {
    Search()
}