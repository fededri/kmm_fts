package com.fededri.kmmfts

import com.fededri.kmmfts.entities.Links
import com.fededri.kmmfts.entities.RocketLaunchJson
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

    private fun multiplyRocketLaunches(launches: List<RocketLaunchJson>): List<RocketLaunchJson> {
        val duplicationCount = 5000

        val duplicatedList = ArrayList<RocketLaunchJson>(launches.size * duplicationCount)
        launches.forEach { item ->
            repeat(duplicationCount) {
                duplicatedList.add(item)
            }
        }

        // create a mock launch for search testing purposes
        val customLaunch = RocketLaunchJson(
            details = "Mock launch",
            flightNumber = 16702,
            links = Links(null, null),
            launchSuccess = true,
            missionName = "Cool mission",
            launchDateUTC = "2021-08-01T00:00:00.000Z"
        )
        return duplicatedList + customLaunch
    }
}