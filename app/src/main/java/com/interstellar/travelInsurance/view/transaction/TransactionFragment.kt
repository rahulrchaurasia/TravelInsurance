package com.interstellar.travelInsurance.view.transaction

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.databinding.FragmentTransactionBinding
import kotlin.math.abs


class TransactionFragment : BaseFragment<FragmentTransactionBinding>(FragmentTransactionBinding::inflate)
{

    //region decleration
    // Override to use custom header
    override val useCustomAppBar: Boolean = true
    private var isToolbarCollapsed = false

   // private lateinit var bottomNavigationView: LinearLayout
   // private var isBottomNavVisible = true

    //endregion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        // Setup the collapsing toolbar
        setupCollapsingToolbar()

        // Initialize other views
        setupViews()


        //MARK : Handle bottom Navigation View hide and show according to nestedScrollview
        attachToNestedScrollView(binding.nestedScrollView)

        // Setup nested scroll listener for bottom navigation bar
       // setUpNestedScrollViewListener()
    }

    //region handle collapsing toolbar setuo
    private fun setupViews() {
        // Setup your buttons and other UI elements
        binding.btnCar.setOnClickListener {
            // Handle button click
        }
    }

    private fun setupCollapsingToolbar() {
        // Set the title in the CollapsingToolbarLayout, not in the Toolbar
        binding.collapsingToolbar.title = ""

        // Setup the toolbar
        binding.toolbar.apply {
            title = "" // Clear title here, as it will be managed by CollapsingToolbarLayout
            // Don't set title here - it will be managed by CollapsingToolbarLayout

            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        // Monitor scroll behavior for visual effects
        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val maxScroll = appBarLayout.totalScrollRange
            val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()



            // Update UI based on scroll percentage
            updateToolbarAppearance(percentage)

            // Check if toolbar is collapsed or expanded
            val isCollapsed = percentage >= 0.9f
            if (isCollapsed != isToolbarCollapsed) {
                isToolbarCollapsed = isCollapsed

                // Set title only when collapsed
                binding.collapsingToolbar.title = if (isCollapsed) "Transactions" else ""
            }
        })
    }

    private fun updateToolbarAppearance(scrollPercentage: Float) {
        // Gradually change toolbar appearance as user scrolls
        // For example, change elevation, text alpha, or background color


        // 1. Elevation effect
        val elevation = scrollPercentage * resources.getDimension(R.dimen._8dp)
        ViewCompat.setElevation(binding.toolbar, elevation)


        // Custom alpha effect
        binding.toolbar.alpha = 1f - (scrollPercentage * 0.5f)

        // 3. Show/hide back arrow based on scroll state
        if (scrollPercentage >= 0.9f) {
            // Collapsed state: Show back arrow and set color to black
            binding.toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_arrow)
            binding.toolbar.navigationIcon?.mutate()?.setTint(ContextCompat.getColor(requireContext(), R.color.black))
        } else {
            // Expanded state: Hide back arrow
            binding.toolbar.navigationIcon = null
        }
    }

    //endregion




}