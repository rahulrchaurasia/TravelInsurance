package com.interstellar.travelInsurance.utils

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.math.abs


//Mark : My  bottomNavigationView: LinearLayout that is c LinearLayout
// which we handle according to nested scroll and recycler Up

//Note : we can used bottomNavigationView: BottomNavigationView most cased used BottomNavigationHelper1
object BottomNavigationHelper {

    private var isBottomNavVisible = true
    private var cumulativeScrollDelta = 0
    private const val SCROLL_THRESHOLD = 5  // Lowered threshold for improved responsiveness
    private const val CUMULATIVE_THRESHOLD = 30 // Detect slow continuous movement


    /**
     * Attach scroll listener to a NestedScrollView
     */
    fun attachToNestedScrollView(
        nestedScrollView: NestedScrollView,
        bottomNavigationView: LinearLayout
    ) {
        nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            val scrollDelta = scrollY - oldScrollY
            cumulativeScrollDelta += scrollDelta

            handleScroll(scrollDelta, cumulativeScrollDelta, bottomNavigationView)
        }
    }

    /**
     * Attach scroll listener to a RecyclerView
     */
    fun attachToRecyclerView(
        recyclerView: RecyclerView,
        bottomNavigationView: LinearLayout
    ) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                cumulativeScrollDelta += dy
                handleScroll(dy, cumulativeScrollDelta, bottomNavigationView)
            }
        })
    }

    /**
     * Handle scroll events and show/hide the bottom navigation bar
     */
    private fun handleScroll(
        scrollDelta: Int,
        cumulativeDelta: Int,
        bottomNavigationView: LinearLayout
    ) {
        if (abs(scrollDelta) > SCROLL_THRESHOLD || abs(cumulativeDelta) > CUMULATIVE_THRESHOLD) {
            if (scrollDelta > 0 || cumulativeDelta > CUMULATIVE_THRESHOLD) {
                if (isBottomNavVisible) hideBottomNavigationView(bottomNavigationView)
            } else if (scrollDelta < 0 || cumulativeDelta < -CUMULATIVE_THRESHOLD) {
                if (!isBottomNavVisible) showBottomNavigationView(bottomNavigationView)
            }

            // Reset cumulative delta after handling
            cumulativeScrollDelta = 0
        }
    }

    /**
     * Hide the bottom navigation bar with animation
     */
    private fun hideBottomNavigationView(bottomNavigationView: LinearLayout) {
        bottomNavigationView.animate()
            .translationY(bottomNavigationView.height.toFloat())
            .alpha(0f) // Fade out effect
            .setDuration(300)
            .withEndAction {
                bottomNavigationView.visibility = View.GONE // Ensure it's completely gone
            }
            .start()
        isBottomNavVisible = false
    }

    /**
     * Show the bottom navigation bar with animation
     */
    private fun showBottomNavigationView(bottomNavigationView: LinearLayout) {
        bottomNavigationView.visibility = View.VISIBLE // Ensure it's visible before animation
        bottomNavigationView.animate()
            .translationY(0f)
            .alpha(1f) // Fade in effect
            .setDuration(300)
            .start()
        isBottomNavVisible = true
    }

    /**
     * Reset the bottom navigation bar to its default state (visible)
     */
    fun resetBottomNavigationView(bottomNavigationView: LinearLayout) {
        bottomNavigationView.visibility = View.VISIBLE
        bottomNavigationView.translationY = 0f
        bottomNavigationView.alpha = 1f
        isBottomNavVisible = true
        cumulativeScrollDelta = 0
    }
}