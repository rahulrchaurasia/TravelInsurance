package com.interstellar.travelInsurance.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.math.RoundingMode
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.text.DecimalFormat
import java.util.*
import android.provider.Settings
import java.text.SimpleDateFormat


//fun Context.showSnackbar(view: View, msg: String?) {
//    Snackbar.make(view, msg ?: "Something went wrong", Snackbar.LENGTH_SHORT).show()
//}


fun Context.showSnackbar(view: View,anchorView : View?, msg: String?) {
    Snackbar.make(view, msg ?: "Something went wrong", Snackbar.LENGTH_SHORT)
        .setAnchorView(anchorView)
        .show()
}

fun Context.showSnackbar(
    view: View,
    anchorView: View?,
    msg: String?,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: String? = null,
    actionListener: View.OnClickListener? = null
) {
    val message = msg ?: "Something went wrong"
    Snackbar.make(view, message, duration)
        .apply {
            anchorView?.let { setAnchorView(it) }
            if (actionText != null && actionListener != null) {
                setAction(actionText, actionListener)
            }
        }
        .show()
}

//fun Context.showSnackbar(
//    view: View,
//    msg: String? = null,
//    actionText: String? = null,
//    actionListener: View.OnClickListener? = null
//) {
//    val message = msg ?: "Something went wrong"
//    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
//
//    if (actionText != null && actionListener != null) {
//        snackbar.setAction(actionText, actionListener)
//    }
//
//    snackbar.show()
//}




fun Context.hideKeyboard(view: View) {
    val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showKeyboard(etOtp: EditText) {
    etOtp.requestFocus()
    val inputMethodManager: InputMethodManager =
        this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(etOtp, InputMethodManager.SHOW_IMPLICIT)
}

fun Context.showKeyboard(view: View) {
    val inputMethodManager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)

}

fun Context.getLocalIpAddress(): String? {
    try {
        val en = NetworkInterface.getNetworkInterfaces()
        while (en.hasMoreElements()) {
            val enumIpAddress = en.nextElement().inetAddresses
            while (enumIpAddress.hasMoreElements()) {
                val inetAddress = enumIpAddress.nextElement()
                if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                    return inetAddress.hostAddress
                }
            }
        }
    } catch (ex: SocketException) {
        ex.printStackTrace()
    }
    return null
}

fun Context.getUniqueID(): String = UUID.randomUUID().toString()

fun Context.twoDecimalFormat(value: Double): String {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.DOWN
    return df.format(value)
}



fun Context.showToast(msg: String) {

    Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
}

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}



// Extension function to apply the allowed characters filter

fun TextInputEditText.setAllowedCharacters(allowedChars: String, imeAction: Int = EditorInfo.IME_ACTION_NEXT) {
    val filter = InputFilter { source, start, end, _, _, _ ->
        source.subSequence(start, end).filter { allowedChars.contains(it) }
    }
    filters = arrayOf(filter)
    imeOptions = imeAction
}


fun Context.getAndroidId(): String {
    return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
}

fun String?.formatCardNumber(): String {
    if (this == null || this.length < 10) return this ?: ""

    val firstSix = this.take(6)
    val lastFour = this.takeLast(4)
    val middleStars = "*".repeat(this.length - 10)

    return "$firstSix$middleStars$lastFour"
}

fun Calendar.formatDate(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(this.time)
}
