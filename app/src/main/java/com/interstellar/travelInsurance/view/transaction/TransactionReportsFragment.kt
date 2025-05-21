package com.interstellar.travelInsurance.view.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.databinding.FragmentTransactionReportsBinding
import com.interstellar.travelInsurance.databinding.FragmentTransactionVerifyBinding
import com.interstellar.travelInsurance.utils.hideKeyboard
import com.interstellar.travelInsurance.view.home.ICustomBackNavigation

/*
For popBack stack to root we has used : 2 option
 best way  used :findNavController().navigate(R.id.action_global_homeFragment)
 or
  findNavController().popBackStack(R.id.homeFragment, false )
 */
class TransactionReportsFragment : BaseFragment<FragmentTransactionReportsBinding>(
    FragmentTransactionReportsBinding::inflate) , OnClickListener , ICustomBackNavigation {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupViews()


    }

    private fun setupViews() {
        // Setup your buttons and other UI elements
        binding.btnSubmit.setOnClickListener(this)
        binding.btnback.setOnClickListener(this)
    }

    private fun setupMenu(){

        val menuHost = requireActivity() as MenuHost

        // Ensure no duplicate MenuProviders
        menuHost.invalidateMenu()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.home_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

              return  when(menuItem.itemId){

                    R.id.home -> {

                        findNavController().popBackStack(R.id.homeFragment, false )
                        true
                    }
                  else -> false


                }

            }



        },viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onClick(view: View?) {

        requireContext().hideKeyboard(binding.root)

        when(view?.id){

            binding.btnSubmit.id -> {

                findNavController().popBackStack(R.id.transactionFragment, false )

            }

            binding.btnback.id -> {

             findNavController().popBackStack()

            }
        }
    }

    override fun onCustomBackPressed(): Boolean {

        //findNavController().popBackStack(R.id.homeFragment, false )

        findNavController().navigate(R.id.action_global_homeFragment)
        return true
    }

}