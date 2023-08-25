package com.fededri.kmmfts

import app.cash.sqldelight.Query
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class LoadResult<T>(
    val data: List<T>,
    val nextKey: Int?
)

class QueryPagingSource<T : Any>(
    private val countQuery: Query<Long>,
    private val searchQueryGetter: (offset: Long, limit: Int) -> Query<T>,
    private val pageSize: Int
) {
    private var backingCount: Long = -1
    private val mutex = Mutex()
    private val loadedPages: MutableMap<Int, List<T>> = mutableMapOf()

    private var callback: ((Int) -> Unit)? = null

    suspend fun load(pageNumber: Int): LoadResult<T> {
        if (count() == 0L) return LoadResult(emptyList(), null)
        val moreData = pageNumber <= (count() / pageSize)


        val page = mutex.withLock {

            val savedPage = loadedPages[pageNumber]
            if (savedPage != null) return@withLock savedPage

            val page = searchQueryGetter(pageNumber.toLong() * pageSize, pageSize).executeAsList()
            page
        }

        callback?.invoke(pageNumber)

        return if (moreData) {
            LoadResult(page, pageNumber + 1)
        } else {
            LoadResult(page, null)
        }
    }

    fun setLoadCallback(callback: (index: Int) -> Unit) {
        this.callback = callback
    }


    suspend fun count(): Long {
        if (backingCount < 0) {
            val count = countQuery.executeAsOne()
            backingCount = count
        }
        return backingCount
    }
}