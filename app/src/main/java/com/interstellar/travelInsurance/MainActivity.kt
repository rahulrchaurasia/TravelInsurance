package com.interstellar.travelInsurance

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.interstellar.travelInsurance.core.facade.SharedPreferenceManager
import com.interstellar.travelInsurance.databinding.ActivityMainBinding
import com.interstellar.travelInsurance.interfaces.AppBarType
import com.interstellar.travelInsurance.interfaces.IHandleAppBar

import com.interstellar.travelInsurance.utils.Constant
import com.interstellar.travelInsurance.view.home.ICustomBackNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/*
Note :  enableEdgeToEdge() my System Bar {ie apps below  bootom bar} default color not chnge when apply on theme.
color not change when apply on theme.That's exactly the issue.
When you use enableEdgeToEdge(), it manages the system bars differently.
The theme's navigationBarColor is being overridden because edge-to-edge mode handles system bars differently.


Common use cases:
android:fitsSystemWindows="true" // for system appbar

android:fitsSystemWindows="false" // for enableEdgeToEdge()

Note: The post method is used to schedule a runnable to be executed on the main (UI) thread after the view is attached.
 */

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), IHandleAppBar {


    @Inject
    lateinit var preferenceManager: SharedPreferenceManager

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var currentAppBarType: AppBarType = AppBarType.NONE

   // private var isToolbarCollapsed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Install splash screen before calling super.onCreate()
       // val splashScreen = installSplashScreen()

        ///region for Full Abar Bar handling

           enableEdgeToEdge()


        setupNavigation()

        setupNavigationListener()

        //  setupCustomToolBarVisibility()

       // setupBottomNavigation()


        //Menu Listener which was not set in graph {ex Logout }
        menuNavigationListner()

        // Back Press Handling
        setupBackPressedDispatcher()


        // appbar scroll effect
        // setupCollapsingToolbar()
    }

    private fun setupSystemBars() {
        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Set initial colors
        window.statusBarColor = getColor(R.color.navigation_bar_color)
        window.navigationBarColor = getColor(R.color.navigation_bar_color)

        // Handle insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply padding to AppBar for status bar
            binding.appbar.setPadding(0, systemBars.top, 0, 0)

            // Apply padding to bottom navigation for navigation bar
            binding.bottomLayer?.setPadding(0, 0, 0, systemBars.bottom)

            windowInsets
        }
    }


    private fun setupEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Set initial colors
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = getColor(R.color.navigation_bar_color)


        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            binding.appbar.setPadding(
                binding.appbar.paddingLeft,
                systemBars.top,
                binding.appbar.paddingRight,
                0
            )

            // Ensure bottomLayer (bottom navigation container) always respects system bars

            binding.bottomLayer?.post {
                binding.bottomLayer.setPadding(0, 0, 0, systemBars.bottom)
            }

            windowInsets
        }

    }

    //region SetUp Navigation and addOnDestinationChangedListener
    private fun setupNavigation() {

        // 1. First set the toolbar as action bar
        // our toolbar come via other layout which is include in main content
        // setSupportActionBar(binding.toolbar)
        setSupportActionBar(binding.defaultToolbar)

        // 2. Get NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController



        // Set the start destination based on user state
        setupStartDestination()

        // 3. Set up AppBarConfiguration
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.profileFragment,
                R.id.reportsFragment,
                R.id.settingFragment


            ),
            binding.drawerLayout
        )

        // 4.  Set up drawer NavigationView with NavController
        binding.navigationView.setupWithNavController(navController)


        // 5. Set up ActionBar with NavController and appBarConfiguration
        setupActionBarWithNavController(navController, appBarConfiguration)


        // 6. Set up BottomNavigationView with NavController
        binding.bottomNavigationView.setupWithNavController(navController)


        // Override the icon
        // supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hemberger_menu_24)

    }

    private fun setupStartDestination() {
        // Inflate the FULL navigation graph
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        if (preferenceManager.isLoggedIn()) {
            // User is logged in, set home_graph as start destination
            navGraph.setStartDestination(R.id.home_graph)
        } else {
            // User is not logged in, set auth_graph as start destination
            navGraph.setStartDestination(R.id.auth_graph)

            // Now configure the auth_graph's start destination
            val authGraph = navGraph.findNode(R.id.auth_graph) as NavGraph

            if (preferenceManager.isFirstTime()) {
                // First time user, show welcome screen
                authGraph.setStartDestination(R.id.welcomeFragment)
            } else {
                // Returning user, show login screen
                authGraph.setStartDestination(R.id.loginFragment)
            }
        }

        // Apply the configured navigation graph
        navController.graph = navGraph
    }


    //Mark :MainActivity should only handle the navigation drawer and bottom navigation
    // navigation Change Listener
    private fun setupNavigationListener() {
        // Initially hide navigation
        hideNavigationDrawer()

       // Mark :MainActivity should only handle the navigation drawer and bottom navigation
        //while the AppBar control should be fully delegated to the fragments via BaseFragment.
        navController.addOnDestinationChangedListener { _, destination, _ ->

            when {
                // Auth Graph - Hide AppBar and Drawer
                destination.parent?.id == R.id.auth_graph -> {
                    // hideAppBar()
                    hideNavigationDrawer()
                    hideBottomNavigation()
                }

                // Fragments with Navigation Drawer
                //{contains the set of destination IDs that should show the navigation drawer (hamburger menu)
                // instead of the back arrow. These are typically your app's top-level/root destinations.
                destination.id in appBarConfiguration.topLevelDestinations -> {

                   // showDefaultAppBar()
                    showNavigationDrawer()
                    showBottomNavigation()
                }

                // Fragments with CustomToolbar and Hide  bottomLayer
                destination.id in customToolbarDestinations -> {

                    // Only handle navigation state
                    showNavigationDrawer()
                    hideBottomNavigation()
                }

                // All other cases - Default to hiding Drawer and AppBar
                else -> {

                    // Only handle drawer and bottom nav
                    hideNavigationDrawer()
                    hideBottomNavigation()

                    //Note :  AppBar will be handled by fragment individually when they are not top level and not in customToolbarDestinations
                }
            }


        }

    }


    private val customToolbarDestinations = setOf(
        R.id.paymentFragment
        // R.id.productDtlFragment,

        // Add other fragments that need custom toolbars
    )
    //endregion

    //region show and hide navigation drawer
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

    //endregion


    // region comment

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
//    fun showincludeDefaultToolbar() {
//        binding.collapsingToolbar.visibility = View.GONE
//        binding.toolbar.visibility = View.VISIBLE
//    }

    //endregion


    //    private fun setupCollapsingToolbar() {
//        // Setup AppBar scroll listener
//        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
//            val scrollRange = appBarLayout.totalScrollRange
//            val percentage = abs(verticalOffset).toFloat() / scrollRange.toFloat()
//
//            // Toggle between expanded and collapsed layouts
//            if (percentage > 0.8f && !isToolbarCollapsed) {
//                isToolbarCollapsed = true
//                binding.collapsedContent.visibility = View.VISIBLE
//                binding.expandedContent.alpha = 0f
//            } else if (percentage < 0.8f && isToolbarCollapsed) {
//                isToolbarCollapsed = false
//                binding.collapsedContent.visibility = View.GONE
//                binding.expandedContent.alpha = 1f
//            }
//
//            // Fade expanded content
//            binding.expandedContent.alpha = 1f - percentage
//        })
//    }
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
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.fragments.firstOrNull()


        if (currentFragment == null) {
            Log.d(Constant.TAG, "No current fragment found")
            return false
        }

        return (currentFragment as? ICustomBackNavigation)?.onCustomBackPressed() ?: false
    }
    //endregion


    //region handle menu item click
    private fun menuNavigationListner() {


        binding.navigationView.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.logout -> {
                    showAlert(
                        title = "Logout",
                        msg = "Are you sure you want to log out?",
                        positiveBtn = resources.getString(R.string.yes),
                        showNegativeButton = true,
                        onPositiveClick = ::handleLogOut
                    )
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
    //endregion

    //region BottomNavigation  setup method

    private fun hideBottomNavigation() {
        binding.bottomLayer.visibility = View.GONE
    }

    private fun showBottomNavigation() {
        binding.bottomLayer.visibility = View.VISIBLE
    }

    private fun setupBottomNavigation() {

        // 6. Set up BottomNavigationView with NavController
        binding.bottomNavigationView.setupWithNavController(navController)

        // Hide bottom nav for specific destinations
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when {
                destination.parent?.id == R.id.auth_graph -> {

                    binding.bottomLayer.visibility = View.GONE
                }

                destination.id in customToolbarDestinations -> {
                    binding.bottomLayer.visibility = View.GONE
                }

                else -> {
                    binding.bottomLayer.visibility = View.VISIBLE
                }
            }
        }

        binding.bottomNavigationView.setOnItemReselectedListener { item ->

            // // Get current destination from NavController
            val currentDestination = navController.currentDestination?.id

            //Mark : Only proceed if we're in the home graph (not auth graph)
            if (navController.currentDestination?.parent?.id == R.id.home_graph) {
                when (item.itemId) {
                    R.id.homeFragment -> {
                        // If we're not already at HomeFragment, pop back to it
                        if (currentDestination != R.id.homeFragment) {
                            navController.popBackStack(R.id.homeFragment, false)
                        } else {
                            // Scroll to top and/or refresh
                            // currentDestination.scrollToTop()
                        }
                    }

                    R.id.transactionFragment -> {
                        // If we're not already at TransactionFragment, pop back to it
                        if (currentDestination != R.id.transactionFragment) {
                            navController.popBackStack(R.id.transactionFragment, false)
                        }
                    }
                    // Add other bottom nav destinations as needed
                }
            }
            //Mark : Because we added all journy fo car is nested Graph whose id is carInsurance_nested_graph
            // hence we have to used nested Graph id name where we used tab in graph we have select start fragment hence it identified  start-uo

            else if (navController.currentDestination?.parent?.id == R.id.carInsurance_nested_graph) {

                // showAlert("tested")
                // when (item.itemId) {
                // R.id.carInsuranceMainFragment -> {
                if (currentDestination != R.id.carInsuranceMainFragment) {
                    navController.popBackStack(R.id.carInsuranceMainFragment, false)
                }
                // }

                // }
            }


        }
    }

    //endregion


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

    // refer from here: https://www.youtube.com/watch?v=1sgRsGUXrNU&list=PLj76U7gxVixTYE8n2X_z50nP3O8TqyBb3&index=12
    private fun navigateToAuth() {

        preferenceManager.clearData()
      // Create bundle with flag
        val args = Bundle().apply {
            putBoolean(Constant.navigateToLogin, true)
        }
        navController.navigate(
            R.id.auth_graph, // Navigate  to auth_graph
            args,
            NavOptions.Builder()
                .setPopUpTo(R.id.home_graph, true) // Clears backstack up to home_graph
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .build()
        )

    }

    //region get TopAppBar

    private fun isTopLevelDestination(destinationId: Int?) =
        destinationId in appBarConfiguration.topLevelDestinations

    //endregion

    //region IHandleAppBar Implementation
    override fun showDefaultAppBar(title: String?) {
        binding.apply {
            // Show AppBar if hidden
            appbar.visibility = View.VISIBLE

            // Show default toolbar, hide custom container
            defaultToolbar.visibility = View.VISIBLE

            // Set title if provided
            title?.let { supportActionBar?.title = it }

            // Setup proper navigation
            setupActionBarWithNavController(navController, appBarConfiguration)
        }
    }



    override fun hideAppBar() {
        binding.appbar.visibility = View.GONE
    }

    //endregion
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

