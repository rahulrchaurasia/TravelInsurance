package com.interstellar.travelInsurance.view.home.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.core.viewmodel.shareViewModel.SharedViewModel
import com.interstellar.travelInsurance.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding> (FragmentProfileBinding::inflate){


    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      //  setToolbarTitle("Profile")
        setupObservers()
    }

    private fun setupObservers() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                sharedViewModel.userName.collect { name ->
                    binding.etUserName.text = name
                }
            }
        }
    }


}