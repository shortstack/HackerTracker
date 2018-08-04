package com.shortstack.hackertracker.ui.schedule.list

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager


abstract class ScheduleInfiniteScrollListener(private val layoutManager: androidx.recyclerview.widget.LinearLayoutManager) : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {

    val visibleThreshold = 5
    var currentPage: Int
    var previousTotalItemCount: Int
    var isLoading: Boolean
    var startingPageIndex = 0

    init {
        currentPage = 0
        previousTotalItemCount = 0
        isLoading = true
    }

    fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    override fun onScrolled(view: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
        var lastVisibleItemPosition = 0
        val totalItemCount = layoutManager.itemCount

        if (layoutManager is androidx.recyclerview.widget.StaggeredGridLayoutManager) {
            val lastVisibleItemPositions = (layoutManager as androidx.recyclerview.widget.StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
            // get maximum element within the list
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
        } else if (layoutManager is androidx.recyclerview.widget.GridLayoutManager) {
            lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        } else if (layoutManager is androidx.recyclerview.widget.LinearLayoutManager) {
            lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        }

        // If the total event count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex
            this.previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                this.isLoading = true
            }
        }
        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total event count.
        if (isLoading && totalItemCount > previousTotalItemCount) {
            isLoading = false
            previousTotalItemCount = totalItemCount
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too
        if (!isLoading && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
            currentPage++
            onLoadMore(currentPage, totalItemCount, view)
            isLoading = true
        }

    }

    abstract fun onLoadMore(page: Int, totalItemsCount: Int, view: androidx.recyclerview.widget.RecyclerView)

}