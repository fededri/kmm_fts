package com.fededri.kmmfts.android

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fededri.kmmfts.QueryPagingSource
import com.fededri.kmmfts.entities.RocketLaunch

class LaunchDataSource(
    private val queryPagingSource: QueryPagingSource<RocketLaunch>
): PagingSource<Int, RocketLaunch>() {
    override fun getRefreshKey(state: PagingState<Int, RocketLaunch>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RocketLaunch> {

        return try {
            val nextPageNumber = params.key ?: 1
            val loadResult = queryPagingSource.load(nextPageNumber)

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