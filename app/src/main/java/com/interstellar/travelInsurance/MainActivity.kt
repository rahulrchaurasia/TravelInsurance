package com.interstellar.travelInsurance

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import coil.size.ViewSizeResolver
import com.interstellar.travelInsurance.databinding.ActivityMainBinding
import com.interstellar.travelInsurance.utils.Constant
import com.interstellar.travelInsurance.view.home.ICustomBackNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {


    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var listener: NavController.OnDestinationChangedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //region Default Code commented
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
         //endregion

        setupNavigation()

        binding.navigationView.isVisible = false

        binding.navigationView.visibility = View.GONE

        listener = NavController.OnDestinationChangedListener{_,destination,_ ->

            binding.navigationView.isVisible = !isLoginDestination(destination.id)
        }

        // Back Press Handling
        setupBackPressedDispatcher()
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
                R.id.loginFragment,
                R.id.registerFragment,
                R.id.homeFragment

                ),
            binding.drawerLayout
        )

        // 4. Set up ActionBar with NavController AND appBarConfiguration
        binding.navigationView.setupWithNavController(navController)


        // 5. Set up NavigationView with NavController
        setupActionBarWithNavController(navController,appBarConfiguration)
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
                if (!handleCustomBackNavigation()) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        })
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
                        onPositiveClick =  ::logout)
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

    private fun isLoginDestination(destinationId: Int): Boolean {
        return when (destinationId) {
            R.id.loginFragment, R.id.registerFragment -> true
            else -> false
        }
    }

    fun logout(){

        showAlert("Logout")
        //sharePrefManager.clearData()
        finish()
//        val intent = Intent(this, LoginActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        startActivity(intent)


    }
}