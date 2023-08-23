package com.fededri.kmmfts

import android.content.Context
import androidx.sqlite.db.SupportSQLiteOpenHelper
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        val factory = RequerySQLiteOpenHelperFactory(emptyList())
        val sqlSchema = AppDatabase.Schema
        return AndroidSqliteDriver(sqlSchema, context, "test.db", factory)
    }
}