package com.interstellar.travelInsurance.view.payment

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.navigation.fragment.findNavController
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.databinding.FragmentPaymentBinding
import com.interstellar.travelInsurance.utils.hideKeyboard
import com.interstellar.travelInsurance.view.home.ICustomBackNavigation


class PaymentFragment : BaseFragment<FragmentPaymentBinding>(FragmentPaymentBinding::inflate),OnClickListener, ICustomBackNavigation {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarTitle("Payment")
        binding.btnBack.setOnClickListener(this)
        binding.btnShare.setOnClickListener(this)
    }

    override fun onClick(view: View?) {

        requireContext().hideKeyboard(binding.root)
        when (view?.id) {

            binding.btnBack.id -> {

                findNavController().popBackStack(R.id.productDtlFragment, false )

            }
            binding.btnShare.id -> {


            }

        }
    }

    override fun onCustomBackPressed(): Boolean {

        findNavController().popBackStack(R.id.productDtlFragment, false )
        return true
    }
}