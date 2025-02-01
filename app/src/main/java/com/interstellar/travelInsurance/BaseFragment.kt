package com.interstellar.travelInsurance

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController

import androidx.viewbinding.ViewBinding
import com.interstellar.travelInsurance.databinding.LayoutLoadingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat


abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (inflator: LayoutInflater) -> VB,
) : Fragment() {


    private lateinit var dialog: Dialog

    private var _binding: VB? = null

    val binding: VB
        get() = _binding as VB


    //region lifecycle

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    //endregion

    protected fun setToolbarTitle(title: String) {
        try {
            (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = title
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun roundOffDecimal(number: Float): String {
        val df = DecimalFormat("#.##")
        //df.roundingMode = RoundingMode.FLOOR

        val value = df.format(number).toFloat()

        return String.format("%.2f", value)
    }

    fun changeStatusColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var window: Window = requireActivity().window
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.setStatusBarColor(ContextCompat.getColor(requireContext(), color))
        }
    }


    fun buttonEnableDisable(isEnable: Boolean, btnToEnableDisable: Button) {

        if (isEnable) {
            btnToEnableDisable.alpha = 1.0f
            btnToEnableDisable.isEnabled = true
        } else {
            btnToEnableDisable.alpha = 0.4f
            btnToEnableDisable.isEnabled = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = bindingInflater.invoke(inflater)
        if (_binding == null)
            throw IllegalArgumentException("Binding cannot be null")

        listenInternetConnectivity()
      //  setupToolbar()

        if (shouldShowCloseButton()) {
            setupMenu()
        }

        return binding.root
    }

    open fun shouldShowCloseButton(): Boolean {
        return false
    }


    //region backPressHandling
    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.close_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menuClose -> {
                        findNavController().popBackStack()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }



    // Move To Root View
    fun moveToHome(){

        findNavController().popBackStack(R.id.homeFragment,false)
    }

    //endregion
    private fun listenInternetConnectivity() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {

                /* baseViewModel.isNetworkAvailable.buffer().collect() {
                     Log.d("IS_INTERNET", "Network available $it")
                 }*/
            }
        }

    }

    //region validators


    fun validateFields(value: String): Boolean {
        return TextUtils.isEmpty(value)
    }

    protected fun isValidMail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    protected fun isValidMobile(phone: String): Boolean {
        return (phone.length == 10) && Patterns.PHONE.matcher(phone).matches()
    }

    //endregion

    //region progress dialog

    fun displayLoadingWithText(
        view: View,
        text: String? = "",
        subText: String? = "",
        cancelable: Boolean? = false,
    ) { // function -- context(parent (reference))

        var loadingLayout: LayoutLoadingBinding? = null
        try {
            if (!this::dialog.isInitialized) {
                dialog = Dialog(requireContext())
                val requestWindowFeature = dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

                loadingLayout = LayoutLoadingBinding.inflate(layoutInflater)
                dialog.setContentView(loadingLayout.root)
                // dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setCancelable(cancelable ?: false)

            }

            loadingLayout?.txtMessage?.text = text
            loadingLayout?.txtDesc?.text = subText

            //hide keyboard
            //view.context.hideKeyboard(view)

            dialog.let {
                if (!it.isShowing) {
                    it.show()
                } else {
                    it.dismiss()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideLoading() {
        try {
            if (this.dialog != null) {
                dialog.dismiss()
            }
        } catch (e: Exception) {
        }
    }

    //endregion


    open fun showAlert(
        msg: String,
        title: String? = null,
        positiveBtn: String? = null,
        negativeBtn: String? = null,
        showNegativeButton: Boolean = false,
        onPositiveClick: (() -> Unit)? = null
    ) {
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(requireActivity(), R.style.AlertDialogTheme)

        alertDialog.apply {
            if (title != null) {
                setTitle(title)
            }
            setMessage(msg)
            setCancelable(false)

            setPositiveButton(positiveBtn ?: "Ok") { dialog, whichButton ->
                onPositiveClick?.invoke()
                dialog.dismiss()
            }

            // Set buttons only if corresponding callback is provided
            if (showNegativeButton) {
                setNegativeButton(negativeBtn ?: "Cancel") { dialog, whichButton ->
                    // onNegativeClick.invoke()
                    dialog.dismiss()
                }
            }

        }.create().show()
    }




}