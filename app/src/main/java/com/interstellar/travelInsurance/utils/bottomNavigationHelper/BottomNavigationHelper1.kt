package com.interstellar.travelInsurance.utils.bottomNavigationHelper

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.math.abs

object BottomNavigationHelper1 {

    private var isBottomNavVisible = true

    /**
     * Attach scroll listener to a NestedScrollView
     */
    fun attachToNestedScrollView(
        nestedScrollView: NestedScrollView,
        bottomNavigationView: BottomNavigationView
    ) {
        nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            val scrollDelta = scrollY - oldScrollY
            handleScroll(scrollDelta, bottomNavigationView)
        }
    }

    /**
     * Attach scroll listener to a RecyclerView
     */
    fun attachToRecyclerView(
        recyclerView: RecyclerView,
        bottomNavigationView: BottomNavigationView
    ) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                handleScroll(dy, bottomNavigationView)
            }
        })
    }

    /**
     * Handle scroll events and show/hide the bottom navigation bar
     */
    private fun handleScroll(scrollDelta: Int, bottomNavigationView: BottomNavigationView) {
        // Add a threshold to prevent flickering during small scrolls
        if (abs(scrollDelta) > 10) {
            if (scrollDelta > 0) {
                // User is scrolling down, hide BottomNavigationView
                if (isBottomNavVisible) {
                    hideBottomNavigationView(bottomNavigationView)
                }
            } else {
                // User is scrolling up, show BottomNavigationView
                if (!isBottomNavVisible) {
                    showBottomNavigationView(bottomNavigationView)
                }
            }
        }
    }

    /**
     * Hide the bottom navigation bar with animation
     */
    private fun hideBottomNavigationView(bottomNavigationView: BottomNavigationView) {
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
    private fun showBottomNavigationView(bottomNavigationView: BottomNavigationView) {
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
    fun resetBottomNavigationView(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.visibility = View.VISIBLE
        bottomNavigationView.translationY = 0f
        bottomNavigationView.alpha = 1f
        isBottomNavVisible = true
    }
}