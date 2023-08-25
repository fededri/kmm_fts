package com.fededri.kmmfts.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.fededri.kmmfts.DatabaseDriverFactory
import com.fededri.kmmfts.SpaceXSDK
import com.fededri.kmmfts.entities.RocketLaunch
import kotlinx.coroutines.cancel

class MainActivity : ComponentActivity() {
    private val spaceXSdk = SpaceXSDK(DatabaseDriverFactory(this))

    private val viewModel: RocketLaunchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val searchQuery = remember { mutableStateOf("") }

            LaunchedEffect(key1 = viewModel, block = {
                viewModel.fetchRocketLaunches(spaceXSdk)
            })

            val state = viewModel.state.collectAsState(initial = State())

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    Column {
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

                        if (state.value.didFinishRequest) {
                            val launches = viewModel.flow(DatabaseDriverFactory(this@MainActivity))
                                .collectAsLazyPagingItems()

                            RocketLaunchesListView(launches = launches)
                        } else {
                           Box(
                               contentAlignment = Alignment.Center,
                               modifier = Modifier.fillMaxSize()
                                   .padding(16.dp)
                           ) {
                                 CircularProgressIndicator()
                           }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RocketLaunchesListView(launches: LazyPagingItems<RocketLaunch>) {
    LazyColumn {
        items(launches.itemCount) { index ->
            val launch = launches[index]

            if (launch != null) {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    backgroundColor = Color.Gray.copy(0.2f)
                ) {
                    Column {
                        Text(
                            text = launch.missionName,
                            modifier = Modifier.padding(8.dp)
                        )
                        Text(
                            text = launch.launchDateUTC,
                            modifier = Modifier.padding(8.dp)
                        )
                        Text(
                            text = launch.details.orEmpty(),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

        }
    }
}
