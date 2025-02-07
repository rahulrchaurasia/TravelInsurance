package com.interstellar.travelInsurance

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.interstellar.travelInsurance.databinding.ActivityMainBinding
import com.interstellar.travelInsurance.interfaces.IHandleAppBar
import com.interstellar.travelInsurance.utils.Constant
import com.interstellar.travelInsurance.view.home.ICustomBackNavigation
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

/*
Note :  enableEdgeToEdge() my System Bar {ie apps below  bootom bar} default color not chnge when apply on theme.
color not change when apply on theme.That's exactly the issue.
When you use enableEdgeToEdge(), it manages the system bars differently.
The theme's navigationBarColor is being overridden because edge-to-edge mode handles system bars differently.


Common use cases:
android:fitsSystemWindows="true" // for system appbar

android:fitsSystemWindows="false" // for enableEdgeToEdge()


 */

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), IHandleAppBar {


    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var isToolbarCollapsed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        ///region for Full Abar Bar ahndling

//        enableEdgeToEdge()
//
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        window.navigationBarColor = getColor(R.color.navigation_bar_color)
//        window.statusBarColor = getColor(R.color.navigation_bar_color)
//
//        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
//            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
//            binding.appbar.setPadding(0, systemBars.top, 0, 0)
//            windowInsets
//        }


         //endregion

// handle apbar from bottom
//        window.navigationBarColor = ContextCompat.getColor(this, R.color.navigation_bar_color)




        setupNavigation()

        setupNavigationAndToolbarVisibility()

      //  setupCustomToolBarVisibility()

        setupBottomNavigation()


        //Menu Listener which was not set in graph {ex Logout }
        menuNavigationListner()

        // Back Press Handling
        setupBackPressedDispatcher()


   // appbar scroll effect
        setupCollapsingToolbar()
    }

    private fun setupNavigation() {

        // 1. First set the toolbar as action bar
        setSupportActionBar(binding.toolbar)

        // 2. Get NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        // 3. Set up AppBarConfiguration
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.transactionFragment,
                R.id.profileFragment,
                R.id.reportsFragment,
                R.id.settingFragment


                ),
            binding.drawerLayout
        )

        // 4.  Set up drawer NavigationView with NavController
        binding.navigationView.setupWithNavController(navController)


        // 5. Set up ActionBar with NavController and appBarConfiguration
        setupActionBarWithNavController(navController,appBarConfiguration)

        // 6. Set up BottomNavigationView with NavController
     //   binding.bottomNavigationView.setupWithNavController(navController)

        // Override the icon
       // supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hemberger_menu_24)

    }

    private fun setupNavigationAndToolbarVisibility() {
        // Initially hide navigation
        hideNavigationDrawer()

        navController.addOnDestinationChangedListener { _, destination, _ ->

            when {
                // Auth Graph - Hide AppBar and Drawer
                destination.parent?.id == R.id.auth_graph -> {
                    hideAppBar()
                    hideNavigationDrawer()
                }

                // Fragments with Navigation Drawer
                destination.id in appBarConfiguration.topLevelDestinations -> {
                    showAppBar()
                    setupActionBarWithNavController(navController, appBarConfiguration)
                    showNavigationDrawer()
                }

                // All other cases - Default to hiding Drawer and AppBar
                else -> {
                   // hideAppBar()
                    hideNavigationDrawer()
                }
            }

            //region comment
            //For Auth Graph
//            if (destination.parent?.id == R.id.auth_graph) {
//                // Auth flow - no appbar, no drawer
//                binding.appbar.visibility = View.GONE
//
//                hideNavigationDrawer()
//            }
//            //For Custom Toolbar {has mostly not a part of Navigation Drawer bec its set fix Configuration
//            else if (destination.id in customToolbarDestinations) {
//                // Specific fragments with custom toolbars
//                binding.appbar.visibility = View.GONE
//
//                hideNavigationDrawer()
//               // for More Specification
////                when (destination.id) {
////                    R.id.productDetailFragment -> hideNavigationDrawer()
////                    R.id.profileFragment -> showNavigationDrawer()
////                }
//            }
//
//            else {
//                // Main flow - default appbar with hamburger
//                binding.appbar.visibility = View.VISIBLE
//                setupActionBarWithNavController(navController, appBarConfiguration)
//
//                showNavigationDrawer()
//            }

            //endregion
        }

    }



    private val customToolbarDestinations = setOf(
        R.id.paymentFragment
       // R.id.productDtlFragment,

        // Add other fragments that need custom toolbars
    )

    private fun hideNavigationDrawer() {
        binding.apply {
            navigationView.visibility = View.GONE
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    private fun showNavigationDrawer() {
        binding.apply {
            navigationView.visibility = View.VISIBLE
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

//
//    fun handleToolbarState(percentage: Float) {
//        binding.apply {
//            when {
//                percentage > 0.9f -> {
//                    toolbar.isVisible = false
//                    collapsingToolbar.isVisible = true
//                }
//                percentage < 0.1f -> {
//                    toolbar.isVisible = true
//                    collapsingToolbar.isVisible = false
//                }
//                else -> {
//                    toolbar.alpha = 1 - percentage
//                    collapsingToolbar.alpha = percentage
//                }
//            }
//        }
//    }

//    fun showCollapsingToolbar() {
//        binding.collapsingToolbar.visibility = View.VISIBLE
//        binding.toolbar.visibility = View.GONE
//    }
//
//    fun showDefaultToolbar() {
//        binding.collapsingToolbar.visibility = View.GONE
//        binding.toolbar.visibility = View.VISIBLE
//    }


    private fun setupCollapsingToolbar() {
        // Setup AppBar scroll listener
        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val scrollRange = appBarLayout.totalScrollRange
            val percentage = abs(verticalOffset).toFloat() / scrollRange.toFloat()

            // Toggle between expanded and collapsed layouts
            if (percentage > 0.8f && !isToolbarCollapsed) {
                isToolbarCollapsed = true
                binding.collapsedContent.visibility = View.VISIBLE
                binding.expandedContent.alpha = 0f
            } else if (percentage < 0.8f && isToolbarCollapsed) {
                isToolbarCollapsed = false
                binding.collapsedContent.visibility = View.GONE
                binding.expandedContent.alpha = 1f
            }

            // Fade expanded content
            binding.expandedContent.alpha = 1f - percentage
        })
    }
    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)


    override fun onSupportNavigateUp(): Boolean {

//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
//        navController = navHostFragment.navController

        return handleCustomBackNavigation() || navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



    //region handBackPress and Navigation UP
    private fun setupBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                //region comment previous
//                if (!handleCustomBackNavigation()) {
//                    isEnabled = false
//                    onBackPressedDispatcher.onBackPressed()
//                    isEnabled = true
//                }
                //endregion

                // 1. First check if drawer is open
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    return
                }

                //2. Check if custom navigation not  handling it
                if (!handleCustomBackNavigation()) {

                    //3. Check if we're at the home fragment (start of home_graph)
                    // If we're at start destination
                    if (isAtHomeFragment()) {
                        showExitConfirmationDialog()
                    } else {
                        // Default back navigation
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                        isEnabled = true
                    }
                }
            }
        })
    }
    private fun isAtHomeFragment(): Boolean {
        return navController.currentDestination?.id == R.id.homeFragment &&
                navController.currentDestination?.parent?.id == R.id.home_graph
    }

    private fun handleCustomBackNavigation(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.fragments.firstOrNull()


        if (currentFragment == null) {
            Log.d(Constant.TAG, "No current fragment found")
            return false
        }

        return (currentFragment as? ICustomBackNavigation)?.onCustomBackPressed() ?: false
    }
    //endregion



    fun menuNavigationListner(){


        binding.navigationView.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.logout -> {
                    showAlert( title = "Logout", msg = "Are you sure you want to log out?",positiveBtn = resources.getString(R.string.yes) , showNegativeButton = true,
                        onPositiveClick =  ::handleLogOut)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)


                    false // Don't navigate
                }
                else -> {
                    // Use the NavigationUI to handle standard destination changes
                    val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)
                    if (handled) {
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    handled
                }
            }

        }
    }

    private fun setupBottomNavigation() {

        // 6. Set up BottomNavigationView with NavController
        binding.bottomNavigationView.setupWithNavController(navController)

        // Hide bottom nav for specific destinations
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when {
                destination.parent?.id == R.id.auth_graph -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
                destination.id in customToolbarDestinations -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }

        binding.bottomNavigationView.setOnItemReselectedListener { item ->
            val currentFragment = navController.currentDestination?.id
            if (currentFragment == item.itemId) {
                // Handle reselection using existing navController
                when (currentFragment) {
                    R.id.homeFragment -> {
                        // Refresh home or scroll to top
                        // Scroll to top and/or refresh
                      //  currentFragment.scrollToTop()

                        navController.popBackStack(R.id.homeFragment, false)

                    }
                    R.id.transactionFragment -> {
                        // Refresh transactions
                       // currentFragment.refreshContent()
                        // Clear back stack of transaction tab and return to main transaction list
                      //  navController.popBackStack(R.id.transactionFragment, false)
                    }
                    // Other cases
                }
            }
        }
    }


    private fun showExitConfirmationDialog() {
        showAlert(
            title = "Exit App",
            msg = "Do you really want to exit?",
            positiveBtn = getString(R.string.yes),
            showNegativeButton = true
        ) {
            finish()
        }
    }

    private fun handleLogOut() {

        // viewModel.logout()
        navigateToAuth()

    }

    private fun navigateToAuth() {
        navController.navigate(
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

    //region Appbar Handling
    override fun hideAppBar() {
        binding.appbar.visibility = View.GONE
    }

    override fun showAppBar() {
        binding.appbar.visibility = View.VISIBLE
    }
    //endregion
}