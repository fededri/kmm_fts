plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("8.0.0").apply(false)
    id("com.android.library").version("8.0.0").apply(false)
    kotlin("android").version("1.8.21").apply(false)
    kotlin("multiplatform").version("1.8.21").apply(false)
    id("app.cash.sqldelight").version("2.0.0-rc02").apply(false)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
