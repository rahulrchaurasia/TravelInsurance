package com.interstellar.travelInsurance.view.carInsurance.main

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.navigation.fragment.findNavController
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.databinding.FragmentCarInsuranceBinding
import com.interstellar.travelInsurance.utils.hideKeyboard


class CarInsuranceMainFragment : BaseFragment<FragmentCarInsuranceBinding> (FragmentCarInsuranceBinding::inflate),OnClickListener{


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnCar.setOnClickListener(this)
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

}




