package com.interstellar.travelInsurance.view.notification

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.R.id.design_bottom_sheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.databinding.BottomSheetNotificationBinding
import com.interstellar.travelInsurance.utils.networkConnect.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationBottomSheet (

    var onVerifyAction: (() -> Unit)? = null

): BottomSheetDialogFragment(), View.OnClickListener {

    companion object {
        const val TAG = "NotificationBottomSheet"
    }
    private var _binding: BottomSheetNotificationBinding? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = BottomSheetNotificationBinding.inflate(inflater,container,false)



        return binding.root
    }

    override fun getTheme(): Int  = R.style.bottomSheetDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.txtNotificationTitle.text = "Notifications"
        binding.txtNotificationContent.text = "You have 3 new messages."

        setUpListner()

    }

    private fun setUpListner(){

        binding.btnSubmit.setOnClickListener(this)
       // binding.imgClose.setOnClickListener(this)

    }

    fun handleBottomSheetExpanded(){

        val bottomSheet = dialog?.findViewById<View>(design_bottom_sheet)
        bottomSheet?.let {
            bottomSheetBehavior = BottomSheetBehavior.from(it)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            // Set up any other BottomSheetBehavior properties you need
            bottomSheetBehavior.isFitToContents = true
            bottomSheetBehavior.expandedOffset = 0

            // Ensure the bottom sheet is fully expanded when shown
            bottomSheetBehavior.peekHeight = resources.displayMetrics.heightPixels
        }

        // Use a Handler to post a delayed runnable that expands the bottom sheet
        Handler(Looper.getMainLooper()).postDelayed({
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }, 400) // 100ms delay, adjust as needed

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {

        when (view?.id) {
            binding.btnSubmit.id -> {

                if (NetworkUtils.isNetworkAvailable(requireContext())) {

                    onVerifyAction?.invoke()

                }



            }



//            binding.imgClose.id -> {
//                //dismiss()
//
//            }
        }
    }

}