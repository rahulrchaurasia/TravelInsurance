package com.interstellar.travelInsurance.view.transaction

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.google.android.material.appbar.AppBarLayout
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.databinding.FragmentTransactionBinding
import kotlin.math.abs


class TransactionFragment : BaseFragment<FragmentTransactionBinding>(FragmentTransactionBinding::inflate)
{

    // Override to use custom header
    override val useCustomAppBar: Boolean = true
    private var isToolbarCollapsed = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

//        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
//            val scrollRange = appBarLayout.totalScrollRange
//            val percentage = abs(verticalOffset).toFloat() / scrollRange.toFloat()
//
//            handleToolbarState(percentage)
//        })



        ////


        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val scrollRange = appBarLayout.totalScrollRange
            val percentage = abs(verticalOffset).toFloat() / scrollRange.toFloat()

            // Toggle between expanded and collapsed layouts
            if (percentage > 0.8f && !isToolbarCollapsed) {
                isToolbarCollapsed = true
                binding.collapsedContent.visibility = View.VISIBLE
                binding.expandedContent.alpha = 0f
            } else if (percentage < 0.8f && isToolbarCollapsed) {
                isToolbarCollapsed = false
                binding.collapsedContent.visibility = View.GONE
                binding.expandedContent.alpha = 1f
            }

            // Fade expanded content
            binding.expandedContent.alpha = 1f - percentage
        })


        ///
    }




    private fun handleToolbarState(percentage: Float) {
        binding.apply {
            when {
                percentage > 0.8f -> {  // Collapsed state
                    toolbar.isVisible = true
                    expandedContent.isVisible = false
                    toolbar.alpha = 1f
                }
                percentage < 0.2f -> {  // Expanded state
                    toolbar.isVisible = false
                    expandedContent.isVisible = true
                    expandedContent.alpha = 1f
                }
                else -> {  // Transition state
                    toolbar.isVisible = true
                    expandedContent.isVisible = true
                    expandedContent.alpha = 1f - percentage
                }
            }
        }
    }




}