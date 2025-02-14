package com.interstellar.travelInsurance.view.carInsurance.detail

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.navigation.fragment.findNavController
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.databinding.FragmentCarDetailBinding
import com.interstellar.travelInsurance.utils.hideKeyboard
import com.interstellar.travelInsurance.view.productDtl.ProductDtlFragmentDirections


class CarDetailFragment : BaseFragment<FragmentCarDetailBinding>(FragmentCarDetailBinding::inflate),
    OnClickListener {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnCar.setOnClickListener(this)
        binding.btnCar1.setOnClickListener(this)
    }



    override fun onClick(view: View?) {

        requireContext().hideKeyboard(binding.root)
        when (view?.id) {

            //Mark : Move to Home { ie Main Home Page Fragment  "Main" intial journey}
            binding.btnCar.id -> {


                //findNavController().popBackStack(R.id.carDetailFragment, true )


                findNavController().popBackStack(R.id.homeFragment, false )


            }

            //Mark :   Move to Car main { ie Car  tab Page  intial journey}
            binding.btnCar1.id -> {


                //findNavController().popBackStack(R.id.carDetailFragment, true )


                findNavController().popBackStack(R.id.carInsuranceMainFragment, false )


            }

        }

    }


}