package com.fededri.kmmfts.android

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fededri.kmmfts.QueryPagingSource
import com.fededri.kmmfts.entities.RocketLaunch
import com.fededri.kmmfts.entities.RocketLaunchJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LaunchDataSource(
    private val queryPagingSource: QueryPagingSource<RocketLaunch>
) : PagingSource<Int, RocketLaunch>() {
    override fun getRefreshKey(state: PagingState<Int, RocketLaunch>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RocketLaunch> =
        withContext(Dispatchers.IO) {
            try {
                val nextPageNumber = params.key ?: 1
                // calculate the elapsed time to load the page
                val currentTime = System.currentTimeMillis()
                val loadResult = queryPagingSource.load(nextPageNumber)
                val elapsedTime = System.currentTimeMillis() - currentTime
                Log.i("LaunchDataSource", "loading page: $nextPageNumber took $elapsedTime ms")

                LoadResult.Page(
                    data = loadResult.data,
                    prevKey = null,
                    nextKey = loadResult.nextKey
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }
}