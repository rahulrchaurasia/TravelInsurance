package com.interstellar.travelInsurance.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.databinding.FragmentProfileBinding


class ProfileFragment : BaseFragment<FragmentProfileBinding> (FragmentProfileBinding::inflate){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarTitle("Profile")
    }




}