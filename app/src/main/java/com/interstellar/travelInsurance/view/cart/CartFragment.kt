package com.interstellar.travelInsurance.view.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.databinding.FragmentCartBinding
import com.interstellar.travelInsurance.utils.hideKeyboard
import com.interstellar.travelInsurance.view.login.LoginFragmentDirections


class CartFragment : BaseFragment<FragmentCartBinding>(FragmentCartBinding::inflate), OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCart.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
        binding.btnHome.setOnClickListener(this)
    }


    override fun onClick(view: View?) {

        requireContext().hideKeyboard(binding.root)
        when (view?.id) {

            binding.btnCart.id -> {

                val action = CartFragmentDirections.actionCartFragmentToPaymentFragment()
                findNavController().navigate(action)

            }
            binding.btnBack.id -> {

                findNavController().popBackStack()

            }

            binding.btnHome.id -> {

                moveToHome()
            }
        }
    }

}