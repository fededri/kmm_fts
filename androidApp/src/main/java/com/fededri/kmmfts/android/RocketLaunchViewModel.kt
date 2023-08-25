package com.fededri.kmmfts.android

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.fededri.kmmfts.DatabaseDriverFactory
import com.fededri.kmmfts.RocketLaunchRepository
import com.fededri.kmmfts.SpaceXSDK
import com.fededri.kmmfts.entities.RocketLaunch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class State(
    val didFinishRequest: Boolean = false,
)

class RocketLaunchViewModel(
) : ViewModel() {


    private var _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: Flow<State>
        get() {
            return _state.asStateFlow()
        }

    fun flow(driverFactory: DatabaseDriverFactory) = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 20)
    ) {
        val repository = RocketLaunchRepository(driverFactory)
        LaunchDataSource(repository.getPaginatedLaunches())
    }.flow
        .cachedIn(viewModelScope)

    fun fetchRocketLaunches(sdk: SpaceXSDK) {
        viewModelScope.launch {
            try {
                sdk.getLaunchesFromServer {
                    _state.value = _state.value.copy(didFinishRequest = true)
                }
            } catch (e: Exception) {
                Log.e("Rocket launch", "something went wrong!")
            }
        }
    }

    fun searchRocketLaunches(searchQuery: String) {
        viewModelScope.launch {

        }
    }
}