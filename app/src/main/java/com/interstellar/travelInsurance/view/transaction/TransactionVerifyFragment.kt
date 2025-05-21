package com.interstellar.travelInsurance.view.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.databinding.FragmentTransactionBinding
import com.interstellar.travelInsurance.databinding.FragmentTransactionVerifyBinding
import com.interstellar.travelInsurance.utils.hideKeyboard


class TransactionVerifyFragment : BaseFragment<FragmentTransactionVerifyBinding>(
    FragmentTransactionVerifyBinding::inflate) , OnClickListener {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
    }

    private fun setupViews() {
        // Setup your buttons and other UI elements
        binding.btnSubmit.setOnClickListener(this)
    }

    override fun onClick(view: View?) {

        requireContext().hideKeyboard(binding.root)

        when(view?.id){

            binding.btnSubmit.id -> {

                val  action = TransactionVerifyFragmentDirections.actionTransactionVerifyFragmentToTransactionReportsFragment()
                findNavController().navigate(action)

            }
        }
    }


}