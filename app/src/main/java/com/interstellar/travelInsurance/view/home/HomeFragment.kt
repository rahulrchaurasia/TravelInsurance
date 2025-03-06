package com.interstellar.travelInsurance.view.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.MainActivity
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.core.facade.SharedPreferenceManager
import com.interstellar.travelInsurance.core.viewmodel.HomeViewModel
import com.interstellar.travelInsurance.databinding.FragmentHomeBinding
import com.interstellar.travelInsurance.interfaces.AppBarType
import com.interstellar.travelInsurance.utils.Constant
import com.interstellar.travelInsurance.utils.hideKeyboard
import com.interstellar.travelInsurance.utils.showSnackbar
import com.interstellar.travelInsurance.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/****************************** Note **************************************************

  PopBack "One Graph to another ie HomeGraph to AuthGrap"
   {HomeFrag to LoginFrag in which homeFrag in HomeGraph and LoginFrag is in AuthGrap }


   Best Practice: Using Global Action (Most Recommended):
   findNavController().navigate(R.id.action_global_to_auth)

   or
   private fun navigateToAuth() {
        findNavController().navigate(
            R.id.auth_graph,
            null, // Bundle of args if needed
            NavOptions.Builder()
                .setPopUpTo(R.id.home_graph, true)
                // Optional animations
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .build()
        )
    }
 */

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding> (FragmentHomeBinding ::inflate) ,
    OnClickListener {

        private val viewModel : HomeViewModel by viewModels()

    @Inject
   lateinit var preferenceManager : SharedPreferenceManager
        private var isBottomNavVisible = true
    private lateinit var bottomNavigationView: LinearLayout


    // Override to set screen title
    override val screenTitle: String = "Home"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.btnProduct.setOnClickListener(this)

        setupMenu()

        setUpNestedScrllViewListnere()

        setupObservers()

//        requireContext().showSnackbar(view = binding.root,
//            anchorView = (activity as? MainActivity)?.findViewById(R.id.bottomNavigationView),
//            msg = "Home Fragment Loaded")

        showSnackbar(msg = "Home Fragment Loaded")

       // viewModel.getData()

      //  appBarHandler?.setAppBar(AppBarType.Default)
    }

//    override val appBarType: AppBarType
//        get() = AppBarType.Default


    //region setUp Menu

    //Note : Home Toolbar Menu Logout way
    private fun setupMenu(){

      //  Home Toolbar Menu not DrawerMenu
        // For Creating Menu
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.logout_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                when(menuItem.itemId){

                    R.id.logout ->{

                        navigateToAuth()

                       // showLogoutConfirmation()


                    }

                }
                return false
            }



        },viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    //endregion

    //region Observer
    private fun setupObservers() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){

                // Observe loading state

//                launch {
//
//                    viewModel.isLoading.collect{isLoading ->
//
//                        displayLoadingWithText(binding.root, "Loading..", null, false)
//                    }
//                }

                // Observe logout events
                launch {
                    viewModel.logoutEvent.collect {
                        navigateToAuth()
                    }
                }
            }
        }
    }
    //endregion

    fun setUpNestedScrllViewListnere(){

        bottomNavigationView = requireActivity().findViewById(R.id.bottomLayer)

        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->

           if (scrollY > oldScrollY) {
               // User is scrolling down, hide BottomNavigationView
               if (isBottomNavVisible) {
                   bottomNavigationView.animate()
                       .translationY(bottomNavigationView.height.toFloat())
                       .setDuration(200)
                       .withEndAction {
                           bottomNavigationView.visibility = View.GONE // Ensure it's completely gone
                       }
                       .start()
                   isBottomNavVisible = false
               }
           } else if (scrollY < oldScrollY) {
               // User is scrolling up, show BottomNavigationView
               if (!isBottomNavVisible) {
                   bottomNavigationView.visibility = View.VISIBLE // Ensure it's visible before animation
                   bottomNavigationView.animate()
                       .translationY(0f)
                       .setDuration(200)
                       .start()
                   isBottomNavVisible = true
               }
           }
       }
    }


    //region Methods
    private fun showLogoutConfirmation() {
        showAlert( title = "Logout", msg = "Are you sure you want to log out?",
            positiveBtn = resources.getString(R.string.yes) , showNegativeButton = true){

            viewModel.logout()
        }

    }
    // Best Practice: Using Global Action (Most Recommended):


    fun scrollToTop() {
//        binding.recyclerView.smoothScrollToPosition(0)
//        // Optionally refresh data
//        viewModel.refreshData()
    }

    //endregion

    //region handling Graph  Official Way (Using NavOptions):
    //Navigate to auth graph and remove all existing graph ie home_graph using setPopUpTo

    //Note : Home Menu Logout way { Not from Navigation View , for Navigation View use Main Activity bec its implement there

    private fun navigateToAuth() {

        preferenceManager.clearData()
        // Create bundle with flag
        val args = Bundle().apply {
            putBoolean(Constant.navigateToLogin, true)
        }
        findNavController().navigate(
            R.id.auth_graph, // Navigate  to auth_graph
            args,
            NavOptions.Builder()
                .setPopUpTo(R.id.home_graph, true) // Clears backstack up to home_graph
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .build()
        )

    }



    //endregion

    //region event
    override fun onClick(view: View?) {

        requireContext().hideKeyboard(binding.root)

        when(view?.id){

            binding.btnProduct.id -> {

                val action =
                    HomeFragmentDirections.actionHomeFragmentToProductDtlFragment()
                findNavController().navigate(action)

            }
        }
    }

    //endregion


}