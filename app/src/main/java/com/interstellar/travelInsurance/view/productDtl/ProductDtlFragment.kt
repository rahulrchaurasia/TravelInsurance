package com.interstellar.travelInsurance.view.productDtl

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.findNavController
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.databinding.FragmentProductDtlBinding
import com.interstellar.travelInsurance.utils.hideKeyboard

/*

clipToPadding="false": Helps smooth scrolling under the collapsing toolbar
fillViewport="true": Ensures content fills screen even if it's short
 */
class ProductDtlFragment : BaseFragment<FragmentProductDtlBinding>(FragmentProductDtlBinding::inflate) ,
    OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




//
      //  appBarHandler?.hideAppBar()
       // setToolbarTitle("Product")

       // binding.btnAddToCart.setOnClickListener(this)
    }

    override fun onClick(view: View?) {

        requireContext().hideKeyboard(binding.root)
//        when (view?.id) {
//
//            binding.btnAddToCart.id -> {
//
//                val action = ProductDtlFragmentDirections.actionProductDtlFragmentToCartFragment()
//                findNavController().navigate(action)
//
//            }
//        }

    }

}