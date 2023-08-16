package com.fededri.kmmfts.android

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fededri.kmmfts.SpaceXSDK
import com.fededri.kmmfts.entities.RocketLaunch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RocketLaunchViewModel : ViewModel() {


    private var _state: MutableStateFlow<List<RocketLaunch>> = MutableStateFlow(emptyList())
    val state: Flow<List<RocketLaunch>>
        get() {
            return _state.asStateFlow()
        }

    fun fetchRocketLaunches(sdk: SpaceXSDK, needReload: Boolean) {
        viewModelScope.launch {
            kotlin.runCatching {
                sdk.getLaunches(needReload)
            }.onSuccess {
                _state.value = it
            }.onFailure {
                Log.e("Rocket launch", "something went wrong!")
            }
        }
    }
}