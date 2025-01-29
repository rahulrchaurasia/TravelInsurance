package com.interstellar.travelInsurance.view.productDtl

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.databinding.FragmentProductDtlBinding
import com.interstellar.travelInsurance.utils.hideKeyboard


class ProductDtlFragment : BaseFragment<FragmentProductDtlBinding>(FragmentProductDtlBinding::inflate) ,
    OnClickListener {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddToCart.setOnClickListener(this)
    }

    override fun onClick(view: View?) {

        requireContext().hideKeyboard(binding.root)
        when (view?.id) {

            binding.btnAddToCart.id -> {

                val action = ProductDtlFragmentDirections.actionProductDtlFragmentToCartFragment()
                findNavController().navigate(action)

            }
        }

    }

}