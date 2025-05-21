package com.interstellar.travelInsurance.view.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.databinding.FragmentTransactionDetailBinding
import com.interstellar.travelInsurance.utils.hideKeyboard
import kotlin.math.abs

//Note: <!--Mark: in CollapsingToolbarLayout  Disable default title animation that is   app:titleEnabled="false" -->
// so default  animation of CollapsingToolbarLayout disable we have to acheive it by toolbar only
class TransactionDetailFragment :
    BaseFragment<FragmentTransactionDetailBinding>(FragmentTransactionDetailBinding::inflate)
    , OnClickListener {

    // Override to use custom header
    override val useCustomAppBar: Boolean = true
    private var isToolbarCollapsed = false



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()

        setupCollapsingToolbar()
    }



    private fun setupViews() {
        // Setup your buttons and other UI elements
        binding.btnSubmit.setOnClickListener(this)
    }
    private fun setupCollapsingToolbar() {

       binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val totalScrollRange = binding.appBarLayout.totalScrollRange
            val collapseRatio = 1 - (abs(verticalOffset).toFloat() / totalScrollRange)

            // Fade Image
            binding.collapsingImage.alpha =  collapseRatio

           // Fade Toolbar
           binding.toolbar.alpha = 1 - collapseRatio


           val elevation = collapseRatio * resources.getDimension(R.dimen._8dp)
           ViewCompat.setElevation(binding.toolbar, elevation)

            // Show/hide MaterialToolbar title
           if (collapseRatio < 0.5f) {

               binding.toolbar.title= "Transaction Details"
               // Collapsed state: Show back arrow and set color to black
               binding.toolbar.navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_back_arrow)
              // binding.toolbar.navigationIcon?.mutate()?.setTint(ContextCompat.getColor(requireContext(), R.color.black))
           } else {

               binding.toolbar.title = ""
               // Expanded state: Hide back arrow
               binding.toolbar.navigationIcon = null
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

    private fun fadeView(view: View, alpha: Float) {
        val animation = AlphaAnimation(view.alpha, alpha)
        animation.duration = 200 // Smooth transition
        animation.fillAfter = true
        view.startAnimation(animation)
    }

    override fun onClick(view: View?) {

        requireContext().hideKeyboard(binding.root)

        when(view?.id){

            binding.btnSubmit.id -> {

                val  action = TransactionDetailFragmentDirections.actionTransactionDetailFragmentToTransactionVerifyFragment()
                findNavController().navigate(action)

            }

            binding.toolbar.id ->{

                findNavController().popBackStack()
            }
        }
    }

}