package com.interstellar.travelInsurance.view.carInsurance.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.databinding.FragmentCarInsuranceBinding
import com.interstellar.travelInsurance.databinding.FragmentCarJourneyBinding
import com.interstellar.travelInsurance.utils.hideKeyboard
import com.interstellar.travelInsurance.view.productDtl.ProductDtlFragmentDirections


class CarJourneyFragment : BaseFragment<FragmentCarJourneyBinding>(FragmentCarJourneyBinding::inflate),
    OnClickListener {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnCar.setOnClickListener(this)
    }



    override fun onClick(view: View?) {

        requireContext().hideKeyboard(binding.root)
        when (view?.id) {

            binding.btnCar.id -> {

                val action = CarJourneyFragmentDirections.actionCarJourneyFragmentToCarDetailFragment()
                findNavController().navigate(action)

            }
        }

    }
}