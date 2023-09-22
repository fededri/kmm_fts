package com.fededri.kmmfts

import com.fededri.kmmfts.entities.RocketLaunch
import com.fededri.kmmfts.entities.RocketLaunchJson


class RocketLaunchRepository(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries
    private val pageSize = 100

    internal fun clearDatabase() {
        dbQuery.transaction {
            dbQuery.removeAllLaunches()
        }
    }

    fun isDatabaseEmpty(): Boolean {
        return dbQuery.isDatabaseEmpty().executeAsOne()
    }

    fun getPaginatedLaunchesBySearch(
        searchQuery: String,
        ftsEnabled: Boolean
    ): QueryPagingSource<RocketLaunch> {
        return if (!ftsEnabled) {
            getNonFtsQuery(searchQuery)
        } else {
            getFtsQuery(searchQuery)
        }
    }

    private fun getFtsQuery(searchQuery: String): QueryPagingSource<RocketLaunch> {
        return QueryPagingSource(
            countQuery = if (searchQuery.isEmpty()) {
                dbQuery.countLaunchesPaginated()
            } else {
                dbQuery.countSearchLaunchesPaginated(searchQuery)
            },
            pageSize = pageSize,
            searchQueryGetter = { offset, limit ->
                if (searchQuery.isEmpty()) {
                    dbQuery.selectLaunchesPaginated(
                        limit.toLong(),
                        offset,
                        mapLaunchSelecting
                    )
                } else {
                    dbQuery.searchLaunchesPaginated(
                        searchQuery,
                        limit.toLong(),
                        offset,
                        mapLaunchSelecting
                    )
                }
            }
        )
    }

    private fun getNonFtsQuery(searchQuery: String): QueryPagingSource<RocketLaunch> {
        return QueryPagingSource(
            countQuery = if (searchQuery.isEmpty()) {
                dbQuery.countLaunchesPaginated()
            } else {
                dbQuery.slowCountSearchLaunchesPaginated(searchQuery)
            },
            pageSize = pageSize,
            searchQueryGetter = { offset, limit ->
                if (searchQuery.isEmpty()) {
                    dbQuery.selectLaunchesPaginated(
                        limit.toLong(),
                        offset,
                        mapLaunchSelecting
                    )
                } else {
                    dbQuery.slowSearchLaunchesPaginated(
                        searchQuery,
                        limit.toLong(),
                        offset,
                        mapLaunchSelecting
                    )
                }
            }
        )
    }

    internal fun createLaunches(launches: List<RocketLaunchJson>) {
        dbQuery.transaction {
            launches.forEach { launch ->
                insertLaunch(launch)
            }
        }
    }

    private fun insertLaunch(launch: RocketLaunchJson) {
        dbQuery.insertLaunch(
            flightNumber = launch.flightNumber.toLong(),
            missionName = launch.missionName,
            details = launch.details,
            launchSuccess = launch.launchSuccess ?: false,
            launchDateUTC = launch.launchDateUTC,
            patchUrlSmall = launch.links.patch?.small,
            patchUrlLarge = launch.links.patch?.large,
            articleUrl = launch.links.article
        )
    }

    private val mapLaunchSelecting: (
        id: Long?,
        missionName: String,
        launchDateUTC: String,
        details: String?,
    ) -> RocketLaunch = { id, missionName, launchDateUTC, details ->
        RocketLaunch(id ?: 1L, missionName, launchDateUTC, details)
    }

}