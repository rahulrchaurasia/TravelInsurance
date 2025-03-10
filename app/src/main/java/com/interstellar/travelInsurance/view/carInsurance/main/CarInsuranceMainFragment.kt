package com.interstellar.travelInsurance.view.carInsurance.main

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.android.material.appbar.AppBarLayout
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.databinding.FragmentCarInsuranceBinding
import com.interstellar.travelInsurance.databinding.LayoutCarHeaderBinding
import com.interstellar.travelInsurance.utils.hideKeyboard
import kotlin.math.abs

//Note :
// useCustomAppBar = true: Use custom header instead of default toolbar
//customAppBarLayoutId: Which layout to inflate for the custom header
class CarInsuranceMainFragment : BaseFragment<FragmentCarInsuranceBinding> (FragmentCarInsuranceBinding::inflate),OnClickListener{

    private var _headerBinding: LayoutCarHeaderBinding? = null
    private val headerBinding get() = _headerBinding!!

    // Override to use custom header
    override val useCustomAppBar: Boolean = true

    private var isToolbarCollapsed = false

    // Sample data for demonstration
    private val productTitle = "Burger"
    private val productPrice = "20Rs"
    private val productOriginalPrice = "30Rs"
    private val productDiscount = "30% Off"



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initData()

        setupCollapsingToolbar()

        setupListener()

        //MARK : Handle bottom Navigation View hide and show according to nestedScrollview
        attachToNestedScrollView(binding.nestedScrollView)

    }

    private fun setupListener() {
        binding.apply {
            btnCar.setOnClickListener(this@CarInsuranceMainFragment)

        }

        // Set up the toolbar navigation click listener
        binding.toolbar.setNavigationOnClickListener {

             findNavController().popBackStack()
        }
    }

    private fun initData(){

        binding.expandedTitle.text = productTitle
        binding.expandedPrice.text = productPrice
        binding.expandedOriginalPrice.text = productOriginalPrice
        binding.discountBadge.text = productDiscount

        // Set initial values for the collapsed content
        binding.collapsedTitle.text = productTitle
        binding.collapsedPrice.text = productPrice
        // binding.collapsedDiscount.text = productDiscount

        // Load the product image into the expanded view

        loadProductImage()
    }


    //region handle Collapsing Layout
    private fun setupCollapsingToolbar() {
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val scrollRange = appBarLayout.totalScrollRange
            val percentage = abs(verticalOffset).toFloat() / scrollRange.toFloat()

            // Toggle between expanded and collapsed layouts
            if (percentage > 0.8f && !isToolbarCollapsed) {
                isToolbarCollapsed = true
                showCollapsedContent()  // Show collapsed content with animation
            } else if (percentage < 0.8f && isToolbarCollapsed) {
                isToolbarCollapsed = false
                showExpandedContent()  // Show expanded content with animation
            }

            // Fade expanded content based on scroll percentage
            binding.expandedContent.alpha = 1f - percentage

            // Handle toolbar visibility and alpha
            handleToolbarState(percentage)
        })
    }

    private fun showCollapsedContent() {
        binding.collapsedContent.visibility = View.VISIBLE
        binding.collapsedContent.alpha = 0f
        binding.collapsedContent.animate()
            .alpha(1f)
            .setDuration(300)  // Duration of the animation
            .setInterpolator(AccelerateDecelerateInterpolator())  // Smooth animation
            .start()

        binding.expandedContent.animate()
            .alpha(0f)
            .setDuration(300)  // Duration of the animation
            .setInterpolator(AccelerateDecelerateInterpolator())  // Smooth animation
            .withEndAction {
                binding.expandedContent.visibility = View.GONE  // Hide after animation
                // Hide individual views in expanded content
                binding.productImage.visibility = View.GONE
                binding.expandedTitle.visibility = View.GONE
                binding.expandedPrice.visibility = View.GONE
                binding.expandedOriginalPrice.visibility = View.GONE
                binding.discountBadge.visibility = View.GONE
            }
            .start()

        // Set the product thumbnail image when collapsed
        binding.productThumb.setImageDrawable(binding.productImage.drawable)
    }

    private fun showExpandedContent() {
        binding.expandedContent.visibility = View.VISIBLE
        binding.expandedContent.alpha = 0f
        binding.expandedContent.animate()
            .alpha(1f)
            .setDuration(300)  // Duration of the animation
            .setInterpolator(AccelerateDecelerateInterpolator())  // Smooth animation
            .start()

        binding.collapsedContent.animate()
            .alpha(0f)
            .setDuration(300)  // Duration of the animation
            .setInterpolator(AccelerateDecelerateInterpolator())  // Smooth animation
            .withEndAction {
                binding.collapsedContent.visibility = View.GONE  // Hide after animation
                // Show individual views in expanded content
                binding.productImage.visibility = View.VISIBLE
                binding.expandedTitle.visibility = View.VISIBLE
                binding.expandedPrice.visibility = View.VISIBLE
                binding.expandedOriginalPrice.visibility = View.VISIBLE
                binding.discountBadge.visibility = View.VISIBLE
            }
            .start()
    }

    private fun handleToolbarState(percentage: Float) {
        binding.apply {
            when {
                percentage > 0.8f -> {  // Collapsed state
                    toolbar.isVisible = true
                    toolbar.alpha = 1f
                }
                percentage < 0.2f -> {  // Expanded state
                    toolbar.isVisible = false
                }
                else -> {  // Transition state
                    toolbar.isVisible = true
                    toolbar.alpha = 1f
                }
            }
        }
    }



    //endregion

    private fun loadProductImage() {
        binding.productImage.load(R.drawable.ic_food) {
            crossfade(true)
            crossfade(300)
            transformations(
                RoundedCornersTransformation(
                    resources.getDimensionPixelSize(R.dimen._10dp).toFloat()
                )
            )
            listener(
                onSuccess = { _, _ ->
                    if (isToolbarCollapsed) {
                        // Set the product thumbnail image when the image is loaded
                        binding.productThumb.setImageDrawable(binding.productImage.drawable)
                    }
                }
            )
        }
    }


    override fun onClick(view: View?) {

        requireContext().hideKeyboard(binding.root)
        when (view?.id) {

            binding.btnCar.id -> {

                val action = CarInsuranceMainFragmentDirections.actionCarInsuranceFragmentToCarJourneyFragment()
                findNavController().navigate(action)
            }
        }

    }

    override fun onDestroyView() {
        // Clean up header binding
                super.onDestroyView()

    }

}




