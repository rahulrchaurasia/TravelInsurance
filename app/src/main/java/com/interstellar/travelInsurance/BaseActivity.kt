package com.interstellar.travelInsurance

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.material.button.MaterialButton
import com.interstellar.travelInsurance.databinding.LayoutLoadingBinding
import com.interstellar.travelInsurance.databinding.NetworkErrorLayoutBinding
import com.interstellar.travelInsurance.utils.ConnectivityObserver
import com.interstellar.travelInsurance.utils.Constant
import com.interstellar.travelInsurance.utils.NetworkConnectivityObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.regex.Pattern


abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private lateinit var dialog: Dialog
    private var dialogNoInterNet: AlertDialog? = null
    private var netWorkErrorLayoutBinding: NetworkErrorLayoutBinding? = null
    lateinit var binding: VB

    private lateinit var connectivityObserver: ConnectivityObserver
    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //    val factory = ViewModelFactory()
        //    viewModel = ViewModelProvider(this, factory).get(getViewModel())
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        binding = getViewBinding()
        //hide keyboard whenever activity launched.
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        setContentView(binding.root)

        connectivityObserver = NetworkConnectivityObserver(applicationContext)

        connectivityObserver.observe()
            .onEach { status ->

                //Log.d(Constant.TAG, "Network Status $status")
                when (status) {
                    ConnectivityObserver.Status.Available -> {
                        hideNoInternetDialog()

                    }

                    else -> {
                        showNoInternetDialog()
                    }
                }
            }
            .launchIn(lifecycleScope)

    }


    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    abstract fun getViewBinding(): VB


    // abstract fun getViewModel(): Class<VM>z


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
                dialog = Dialog(this)
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


    //region validate edittext

    fun validateFields(value: String): Boolean {
        return TextUtils.isEmpty(value)
    }


    protected fun isValidMobile(phone: String): Boolean {
        return (phone.length == 10) && Patterns.PHONE.matcher(phone).matches()
    }

    protected fun isValidEmail(str: String): Boolean {
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }

    protected fun getCurrentDateTimeAccordingToUTC(format: String): String? {
        val date = Date(System.currentTimeMillis())
        val simpleDateFormat = SimpleDateFormat(format)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return simpleDateFormat.format(date)
    }

    //endregion
    internal fun EditText.showKeyboard() {
        if (requestFocus()) {
            (context?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as?
                    InputMethodManager)!!
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }


    open fun showAlert(
        msg: String,
        title: String? = null,
        positiveBtn: String? = null,
        negativeBtn: String? = null,
        showNegativeButton: Boolean = false,
        onPositiveClick: (() -> Unit)? = null,

        ) {

        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogTheme)

        alertDialog.apply {


            if (title != null) {
                setTitle(title)
            }
            setMessage(msg)
            setCancelable(false)

            setPositiveButton("OK") { dialog, whichButton ->
                onPositiveClick?.invoke()
                dialog.dismiss()
            }


            // Set buttons only if corresponding callback is provided
            if (showNegativeButton) {
                setNegativeButton(negativeBtn ?: "Cancel") { dialog, whichButton ->
                    //onNegativeClick.invoke()
                    dialog.dismiss()
                }
            }

        }.create().show()
    }


    private fun showNoInternetDialog() {
        if (dialogNoInterNet == null) {
            netWorkErrorLayoutBinding = NetworkErrorLayoutBinding.inflate(layoutInflater)

            dialogNoInterNet = AlertDialog.Builder(this)
                .setView(netWorkErrorLayoutBinding?.root)
                .setCancelable(false)
                .create()

//            netWorkErrorLayoutBinding?.btnRetry?.setOnClickListener {
//                // Add retry logic here
//                hideNoInternetDialog()
//            }
        }

        dialogNoInterNet?.let {
            if (!it.isShowing) {
                it.show()
            }
        }
    }

    private fun hideNoInternetDialog() {

        if (dialogNoInterNet != null) {

            dialogNoInterNet?.dismiss()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        hideNoInternetDialog()
        netWorkErrorLayoutBinding = null
    }


}