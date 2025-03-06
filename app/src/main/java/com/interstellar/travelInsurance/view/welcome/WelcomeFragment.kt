package com.interstellar.travelInsurance.view.welcome

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.core.facade.SharedPreferenceManager
import com.interstellar.travelInsurance.core.model.WelcomeScreenItem
import com.interstellar.travelInsurance.databinding.FragmentWelcomeBinding
import com.interstellar.travelInsurance.utils.Constant
import com.interstellar.travelInsurance.view.login.LoginFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>(FragmentWelcomeBinding::inflate){


    @Inject
    lateinit var preferenceManager: SharedPreferenceManager



    private lateinit var adapter: WelcomePagerAdapter
    private val welcomeItems = mutableListOf<WelcomeScreenItem>()


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Check if we should navigate directly to login
//        handleDirectToLoginNavigation()
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        handleDirectToLoginNavigation()

        // Prepare welcome screens data
        loadWelcomeScreensData()


        // Set up ViewPager
        setupViewPager()

        //dot indicator atach to ViewPager
        binding.dotsIndicator.attachTo(binding.viewPager)

        // Set up buttons
        setupButtons()
    }

    private fun handleDirectToLoginNavigation() {
        // Check if we're coming from logout
        arguments?.let { args ->
            if (args.getBoolean(Constant.navigateToLogin, false)) {

                // Ensure navigation happens only once per instance
              //  args.remove(Constant.navigateToLogin)

                // Create a new Bundle with existing arguments
                val newArgs = Bundle(args)
                newArgs.remove(Constant.navigateToLogin)  // Modify the copy

                // Set modified arguments back to fragment
                arguments = newArgs

                val action =   WelcomeFragmentDirections.actionWelcomeToLogin()
                findNavController().navigate(action)
//                Handler(Looper.getMainLooper()).postDelayed({
//                    val action =   WelcomeFragmentDirections.actionWelcomeToLogin()
//                    findNavController().navigate(action)
//                }, 200)  // 100ms delay to allow apply() to persist



                //findNavController().navigate(R.id.action_welcome_to_login)
                // Post to next frame to ensure fragment is fully attached
//                view?.post {
//                    // Navigate to login immediately
//                    findNavController().navigate(R.id.action_welcome_to_login)
//                }
            }
        }
    }

    private fun loadWelcomeScreensData() {
        welcomeItems.clear()
        welcomeItems.add(
            WelcomeScreenItem(
                R.drawable.welcome_image_1,
                "Travel Without Worries",
                "Get comprehensive travel insurance coverage for your journeys around the world"
            )
        )
        welcomeItems.add(
            WelcomeScreenItem(
                R.drawable.welcome_image_2,
                "Easy Claims Process",
                "File claims quickly and easily with our streamlined digital process"
            )
        )
        welcomeItems.add(
            WelcomeScreenItem(
                R.drawable.welcome_image_3,
                "24/7 Support",
                "Access our support team anytime, anywhere during your travels"
            )
        )

        welcomeItems.add(
            WelcomeScreenItem(
                R.drawable.welcome_image_4,
                "Secure Payment Options",
                "Multiple secure payment methods to choose and manage your insurance plans easily"
            )
        )
    }



    private fun setupViewPager() {



        adapter = WelcomePagerAdapter(welcomeItems)
        binding.viewPager.adapter = adapter

        // Connect TabLayout (dots indicator) with ViewPager2

        // Page change listener to update button visibility
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtonsForPosition(position)
            }
        })
    }

    private fun setupButtons() {
        // Initial button state
        updateButtonsForPosition(0)

        // Skip button - go directly to login
        binding.skipButton.setOnClickListener {
            navigateToLogin()
        }

        // Next button
        binding.nextButton.setOnClickListener {
            val currentPosition = binding.viewPager.currentItem
            if (currentPosition < welcomeItems.size - 1) {
                binding.viewPager.currentItem = currentPosition + 1
            }
        }

        // Previous button
        binding.prevButton.setOnClickListener {
            val currentPosition = binding.viewPager.currentItem
            if (currentPosition > 0) {
                binding.viewPager.currentItem = currentPosition - 1
            }
        }

        // Get Started button
        binding.getStartedButton.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun updateButtonsForPosition(position: Int) {
        val isLastPage = position == welcomeItems.size - 1

        // Show/hide navigation buttons
       // binding.prevButton.visibility = if (position > 0) View.VISIBLE else View.GONE
        binding.nextButton.visibility = if (isLastPage) View.GONE else View.VISIBLE
        binding.getStartedButton.visibility = if (isLastPage) View.VISIBLE else View.GONE

        // Hide skip button on last page
        binding.skipButton.visibility = if (isLastPage) View.GONE else View.VISIBLE
    }

    private fun navigateToLogin() {
        // Mark welcome screens as shown
        preferenceManager.setFirstTime(false)

        // Navigate to login
        //        //findNavController().navigate(R.id.action_welcome_to_login)

        val action =   WelcomeFragmentDirections.actionWelcomeToLogin()
        findNavController().navigate(action)

    }


}