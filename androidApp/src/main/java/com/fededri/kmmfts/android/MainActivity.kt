package com.fededri.kmmfts.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.fededri.kmmfts.DatabaseDriverFactory
import com.fededri.kmmfts.SpaceXSDK
import com.fededri.kmmfts.entities.RocketLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val spaceXSdk = SpaceXSDK(DatabaseDriverFactory(this))

    private val viewModel: RocketLaunchViewModel by viewModels()
    private val driver by lazy { DatabaseDriverFactory(this@MainActivity) }

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
                        ThrottledTextView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            callback = {
                                searchQuery.value = it
                            },
                            label = {
                                Text("Search rocket launches")
                            })

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(checked = state.value.ftsEnabled, onCheckedChange = {
                                viewModel.toggleFts(it)
                            })
                            Text(text = "Toggle FTS")
                        }


                        if (state.value.didFinishDownloadingLaunches) {
                            Log.i("MainActivity", "collecting flow")
                            val pagedItems = viewModel.flow(
                                driver,
                                searchQuery.value
                            )
                                .collectAsLazyPagingItems()

                            RocketLaunchesListView(launches = pagedItems)
                        } else {
                            LoadingView()
                        }
                    }
                }
            }
        }
    }
}

// Circular progress indicator
@Composable
fun LoadingView() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CircularProgressIndicator()
    }
}

// A text view that only updates when the user stops typing for 300ms.
@Composable
fun ThrottledTextView(
    callback: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit
) {
    val text = remember { mutableStateOf("") }

    var job by remember { mutableStateOf<Job?>(null) }

    TextField(
        value = text.value,
        onValueChange = {
            job?.cancel()
            job = CoroutineScope(Dispatchers.IO).launch {
                delay(300)
                callback(it)
            }
            text.value = it
        },
        label = label,
        modifier = modifier
    )
}

@Composable
fun RocketLaunchesListView(launches: LazyPagingItems<RocketLaunch>) {
    if (launches.loadState.refresh is LoadState.Loading) {
        LoadingView()
    } else {
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
}
