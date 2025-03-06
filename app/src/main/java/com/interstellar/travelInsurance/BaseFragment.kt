package com.interstellar.travelInsurance

import android.app.Dialog
import android.content.Context
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
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController

import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.interstellar.travelInsurance.databinding.LayoutLoadingBinding

import com.interstellar.travelInsurance.interfaces.AppBarHandlerOld
import com.interstellar.travelInsurance.interfaces.AppBarType
import com.interstellar.travelInsurance.interfaces.IHandleAppBar

import com.interstellar.travelInsurance.utils.showSnackbar
import kotlinx.coroutines.launch
import java.text.DecimalFormat


abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (inflator: LayoutInflater) -> VB,
) : Fragment() {


    private lateinit var dialog: Dialog

    private var _binding: VB? = null

    val binding: VB
        get() = _binding as VB



    //Note : Since using of abract every child has to implemt that field or methjod.
    // hee when we wan to force every fragment to implement and define appbar use below
   // abstract val appBarType: AppBarType

   // OR we can set Default ie it apply to all child Frag of Base Class




    /////

    protected var appBarHandler: IHandleAppBar? = null

    // Default configuration - can be overridden by all fragments
    protected open val useCustomAppBar: Boolean = false //// Default to using MainActivity's AppBar
    protected open val screenTitle: String? = null // Default to no title


    protected val bottomView: View?
        get() = activity?.findViewById(R.id.bottomLayer)



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Note :u can set here for apply default to all and override "appBarType" if wann  change : or manually set to each frag using appBarHandler?.setAppBar(appBarType)
       // appBarHandler?.setAppBar(appBarType)  (Optional : for Default set every fragment)

        setupAppBar()
    }

    override fun onDetach() {
        super.onDetach()

        this.appBarHandler = null
    }

    //region lifecycle

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    //endregion

    //  Mark :MainActivity should only handle the navigation drawer and bottom navigation
    //while the AppBar control should be fully delegated to the fragments via BaseFragment.

    private fun setupAppBar() {
        appBarHandler?.let { handler ->
            when {

                // Check if we're in the auth graph
                findNavController().currentDestination?.parent?.id == R.id.auth_graph -> {
                    handler.hideAppBar()
                }
                // For fragments that need custom toolbar
                useCustomAppBar -> {
                    handler.hideAppBar() // Fragment will show its own toolbar
                }
                // For fragments that use default toolbar
                else -> {
                    handler.showDefaultAppBar(screenTitle)
                }
            }
        }
    }
    private fun setupAppBar1() {
        appBarHandler?.let { handler ->

            if (useCustomAppBar) {
                handler.hideAppBar() // Hide MainActivity's AppBar
            } else {
                handler.showDefaultAppBar(screenTitle) // Show default AppBar ,// Use MainActivity's AppBar with optional title
            }


        }
    }

    // Utility method to update toolbar title (only works with default AppBar)
    protected fun updateToolbarTitle(title: String) {
        if (!useCustomAppBar) {
            appBarHandler?.showDefaultAppBar(title)
        }
    }

    fun roundOffDecimal(number: Float): String {
        val df = DecimalFormat("#.##")
        //df.roundingMode = RoundingMode.FLOOR

        val value = df.format(number).toFloat()

        return String.format("%.2f", value)
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is IHandleAppBar) {
            this.appBarHandler = context

        }


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


    // Base showSnackbar function that all fragments can use
    protected fun showSnackbar(
        msg: String?,
        duration: Int = Snackbar.LENGTH_SHORT,
        actionText: String? = null,
        actionListener: View.OnClickListener? = null
    ) {
        requireContext().showSnackbar(
            view = requireView(),
            anchorView = bottomView,
            msg = msg,
            actionText = actionText,
            actionListener = actionListener
        )
    }


}