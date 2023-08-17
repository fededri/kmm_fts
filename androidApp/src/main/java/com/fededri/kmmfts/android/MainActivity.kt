package com.fededri.kmmfts.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fededri.kmmfts.DatabaseDriverFactory
import com.fededri.kmmfts.Greeting
import com.fededri.kmmfts.SpaceXSDK
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val mainScope = MainScope()
    private val spaceXSdk = SpaceXSDK(DatabaseDriverFactory(this))

    private val viewModel: RocketLaunchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val searchQuery = remember { mutableStateOf("") }

            LaunchedEffect(key1 = viewModel, block = {
                viewModel.fetchRocketLaunches(spaceXSdk, true)
            })

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column { // Wrap the content in a Column
                        TextField(
                            value = searchQuery.value,
                            onValueChange = {
                                searchQuery.value = it
                            },
                            label = { Text("Search Rocket Launches") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )

                        val state = viewModel.state.collectAsState(initial = emptyList())


                        val filteredLaunches = state.value.filter {
                            it.missionName.contains(searchQuery.value, true) ||
                                    it.launchDateUTC.contains(searchQuery.value, true) ||
                                    it.details?.contains(searchQuery.value, true) == true
                        }

                        LazyColumn(content = {
                            items(filteredLaunches) {
                                Card(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    backgroundColor = Color.Gray.copy(0.2f)
                                ) {
                                    Column {
                                        Text(text = it.missionName, modifier = Modifier.padding(8.dp))
                                        Text(text = it.launchDateUTC, modifier = Modifier.padding(8.dp))
                                        Text(
                                            text = it.details.orEmpty(),
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            }
                        })
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}
