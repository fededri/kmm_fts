package com.fededri.kmmfts.android

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
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
    val didFinishDownloadingLaunches: Boolean = false,
    val ftsEnabled: Boolean = false,
    val isLoading: Boolean = false
)

class RocketLaunchViewModel(
) : ViewModel() {

    private var _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: Flow<State>
        get() {
            return _state.asStateFlow()
        }

    fun flow(
        driverFactory: DatabaseDriverFactory,
        searchQuery: String = ""
    ): Flow<PagingData<RocketLaunch>> {
        return Pager(
            // Configure how data is loaded by passing additional properties to
            // PagingConfig, such as prefetchDistance.
            PagingConfig(pageSize = 20)
        ) {
            val repository = RocketLaunchRepository(driverFactory)
            Log.i("RocketLaunchViewModel", "generating data source, fts enabled ${_state.value.ftsEnabled}")
            LaunchDataSource(
                repository.getPaginatedLaunchesBySearch(
                    searchQuery,
                    _state.value.ftsEnabled
                )
            )
        }.flow
    }

    fun toggleFts(enabled: Boolean) {
        _state.value = _state.value.copy(ftsEnabled = enabled)
    }

    fun fetchRocketLaunches(sdk: SpaceXSDK) {
        viewModelScope.launch {
            try {
                sdk.getLaunchesFromServer {
                    _state.value = _state.value.copy(didFinishDownloadingLaunches = true)
                }
            } catch (e: Exception) {
                Log.e("Rocket launch", "something went wrong!")
            }
        }
    }
}