package com.fededri.kmmfts

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform