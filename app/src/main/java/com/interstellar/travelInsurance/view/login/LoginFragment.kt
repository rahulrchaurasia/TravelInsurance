package com.interstellar.travelInsurance.view.login

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.core.facade.SharedPreferenceManager
import com.interstellar.travelInsurance.databinding.FragmentLoginBinding
import com.interstellar.travelInsurance.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/*
Remove app:passwordToggleEnabled (Deprecated)
Use endIconMode="password_toggle.


🔥 Summary  VVIMP
findNavController() → Only navigates within the same graph.
requireActivity().findNavController(R.id.nav_host_fragment) → Used when switching between different navigation graphs.

 */

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) , OnClickListener{
    // TODO: Rename and change types of parameters



    @Inject
    lateinit var preferenceManager: SharedPreferenceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // Optional: Set full screen for auth screens
        //WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        // Optional: Handle system bar colors
//        requireActivity().window.apply {
//            statusBarColor = Color.TRANSPARENT
//            navigationBarColor = Color.TRANSPARENT
//        }

        binding.btnLogin.setOnClickListener(this)
        binding.btnRegister.setOnClickListener(this)
    }

    override fun onClick(view: View?) {

        requireContext().hideKeyboard(binding.root)
        when (view?.id) {

            binding.btnLogin.id -> {

                preferenceManager.setLoggedIn(true)


                findNavController().navigate(R.id.home_graph, null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.auth_graph
                            , true)
                        .setEnterAnim(R.anim.slide_in_right)
                        .setExitAnim(R.anim.slide_out_left)
                        .build()
                )

               // region 2>  Using Global action move to home graph

                //findNavController().navigate(R.id.action_global_to_home)

                //endregion


                //************************************************************
                //Mark : Works from anywhere inside the activity or fragment.
                // Get the NavController from the Activity
                //************************************************************
                //region 3 Works from anywhere inside the activity or fragment.
                //                val navController = requireActivity().findNavController(R.id.navHostFragment)
//
//                    // Navigate to the home_graph
//                    .navigate(R.id.home_graph,null,
//                        NavOptions.Builder()
//                        .setPopUpTo(R.id.auth_graph,true) // Clears login graph from backstack
//                        .setEnterAnim(R.anim.slide_in_right)
//                        .setExitAnim(R.anim.slide_out_left)
//                        .build()
//                    )
                //endregion


            }

            binding.btnRegister.id -> {

                    val action =   LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                    findNavController().navigate(action)
                }
            }
        }



}