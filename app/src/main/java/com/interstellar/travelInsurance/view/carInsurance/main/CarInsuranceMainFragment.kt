package com.interstellar.travelInsurance.view.carInsurance.main

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.databinding.FragmentCarInsuranceBinding
import com.interstellar.travelInsurance.databinding.LayoutCarHeaderBinding
import com.interstellar.travelInsurance.utils.hideKeyboard

//Note :
// useCustomAppBar = true: Use custom header instead of default toolbar
//customAppBarLayoutId: Which layout to inflate for the custom header
class CarInsuranceMainFragment : BaseFragment<FragmentCarInsuranceBinding> (FragmentCarInsuranceBinding::inflate),OnClickListener{

    private var _headerBinding: LayoutCarHeaderBinding? = null
    private val headerBinding get() = _headerBinding!!

    // Override to use custom header

    override val useCustomAppBar: Boolean = true
    override val customAppBarLayoutId: Int = R.layout.layout_car_header


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCustomHeader()

        setupScrollBehavior()   // handle Scroll effect

        setupListener()
    }

    private fun setupListener() {
        binding.apply {
            btnCar.setOnClickListener(this@CarInsuranceMainFragment)

        }
    }


    private fun setupCustomHeader() {
        // Get the custom header container
        try {
            val headerContainer = requireActivity().findViewById<FrameLayout>(R.id.customHeaderContainer)

            // Inflate the custom header using ViewBinding
            _headerBinding = LayoutCarHeaderBinding.inflate(
                layoutInflater,
                headerContainer,
                true
            )

            // Setup header views using binding
            headerBinding.apply {
                // Setup navigation
//                btnBack.setOnClickListener(this@CarFragment)
//
//                // Setup car details
//                carModel.text = "Honda City"
//                carNumber.text = "MH-02-AB-1234"
//

                // Update UI elements
                txtLocationText.text = "Your Location"
                txtLocationName.text = "Mumbai"
//                // Setup other header views

                garageButton.setOnClickListener(this@CarInsuranceMainFragment)
                searchBar.setOnClickListener(this@CarInsuranceMainFragment)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupScrollBehavior() {
        // Ensure proper nested scrolling
        binding.root.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            // Handle scroll state changes if needed
            val appBarLayout = requireActivity().findViewById<AppBarLayout>(R.id.appbar)
            if (scrollY > oldScrollY) {
                // Scrolling up - collapse header
                appBarLayout.setExpanded(false, true)
            } else if (scrollY < oldScrollY) {
                // Scrolling down - expand header
                appBarLayout.setExpanded(true, true)
            }
        })
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
        _headerBinding = null
        // Remove header views
        requireActivity().findViewById<FrameLayout>(R.id.customHeaderContainer)?.removeAllViews()
        super.onDestroyView()    }

}




