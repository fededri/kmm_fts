package com.fededri.kmmfts

import kotlin.Boolean
import kotlin.Long
import kotlin.String

public data class Launch(
  public val flightNumber: Long,
  public val missionName: String,
  public val details: String?,
  public val launchSuccess: Boolean?,
  public val launchDateUTC: String,
  public val patchUrlSmall: String?,
  public val patchUrlLarge: String?,
  public val articleUrl: String?
) {
  public override fun toString(): String = """
  |Launch [
  |  flightNumber: $flightNumber
  |  missionName: $missionName
  |  details: $details
  |  launchSuccess: $launchSuccess
  |  launchDateUTC: $launchDateUTC
  |  patchUrlSmall: $patchUrlSmall
  |  patchUrlLarge: $patchUrlLarge
  |  articleUrl: $articleUrl
  |]
  """.trimMargin()
}
