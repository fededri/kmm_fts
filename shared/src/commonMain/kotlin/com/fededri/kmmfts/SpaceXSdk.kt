package com.fededri.kmmfts

import com.fededri.kmmfts.entities.RocketLaunch
import com.fededri.kmmfts.network.SpaceXApi


class SpaceXSDK(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = RocketLaunchRepository(databaseDriverFactory)
    private val api = SpaceXApi()


    @Throws(Exception::class)
    suspend fun getLaunches(forceReload: Boolean): List<RocketLaunch> {
        val cachedLaunches = database.getAllLaunches()
        return if (cachedLaunches.isNotEmpty() && !forceReload) {
            cachedLaunches
        } else {
            api.getAllLaunches().also {
                database.clearDatabase()
                database.createLaunches(it)
            }
        }
    }
}