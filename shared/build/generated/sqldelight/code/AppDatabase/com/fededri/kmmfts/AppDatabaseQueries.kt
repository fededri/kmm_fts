package com.fededri.kmmfts

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlin.Unit

public interface AppDatabaseQueries : Transacter {
  public fun <T : Any> selectAllLaunchesInfo(mapper: (
    flightNumber: Long,
    missionName: String,
    details: String?,
    launchSuccess: Boolean?,
    launchDateUTC: String,
    patchUrlSmall: String?,
    patchUrlLarge: String?,
    articleUrl: String?
  ) -> T): Query<T>

  public fun selectAllLaunchesInfo(): Query<Launch>

  public fun insertLaunch(
    flightNumber: Long,
    missionName: String,
    details: String?,
    launchSuccess: Boolean?,
    launchDateUTC: String,
    patchUrlSmall: String?,
    patchUrlLarge: String?,
    articleUrl: String?
  ): Unit

  public fun removeAllLaunches(): Unit
}
