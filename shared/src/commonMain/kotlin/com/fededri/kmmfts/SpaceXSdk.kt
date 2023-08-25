package com.fededri.kmmfts

import com.fededri.kmmfts.entities.RocketLaunch
import com.fededri.kmmfts.network.SpaceXApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext


class SpaceXSDK(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = RocketLaunchRepository(databaseDriverFactory)
    private val api = SpaceXApi()


    @Throws(Exception::class)
    suspend fun getLaunchesFromServer(callback: () -> Unit) =
        withContext(Dispatchers.IO) {
            if (database.isDatabaseEmpty()) {
                api.getAllLaunches().also {
                    database.clearDatabase()
                    // We just want more data in the database
                    val duplicatedLaunches = multiplyRocketLaunches(it)
                    database.createLaunches(duplicatedLaunches)
                    callback()
                }
            } else {
                callback()
            }
        }

    private fun multiplyRocketLaunches(launches: List<RocketLaunch>): List<RocketLaunch> {
        val duplicationCount = 1500

        val duplicatedList = ArrayList<RocketLaunch>(launches.size * duplicationCount)
        launches.forEach { item ->
            repeat(duplicationCount) {
                duplicatedList.add(item)
            }
        }

        return duplicatedList
    }
}