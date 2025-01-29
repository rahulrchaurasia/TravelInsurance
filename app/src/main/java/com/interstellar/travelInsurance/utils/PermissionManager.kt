
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.utils.Permission

import java.lang.ref.WeakReference


class PermissionManager private constructor(private val fragment: WeakReference<Fragment>) {
    private val requiredPermissions = mutableListOf<Permission>()
    private var rationale: String? = null
    private var callback: (Boolean) -> Unit = {}
    private var detailedCallback: (Map<Permission, Boolean>) -> Unit = {}

    private val permissionCheck = fragment.get()
        ?.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantResults ->
            sendResultAndCleanUp(grantResults)
        }

    companion object {
        fun from(fragment: Fragment) = PermissionManager(WeakReference(fragment))
    }

    fun rationale(description: String): PermissionManager {
        rationale = description
        return this
    }

    fun request(vararg permission: Permission): PermissionManager {
        requiredPermissions.addAll(permission)
        return this
    }

    fun checkPermission(callback: (Boolean) -> Unit) {
        this.callback = callback
        handlePermissionRequest()
    }

    fun checkDetailedPermission(callback: (Map<Permission, Boolean>) -> Unit) {
        this.detailedCallback = callback
        handlePermissionRequest()
    }

    private fun handlePermissionRequest() {
        fragment.get()?.let { fragment ->
            when {
                arePermissionsGranted(fragment) -> sendPositiveResult()
                shouldShowPermissionRationale(fragment) -> displayRationale(fragment)
                else -> requestPermissions()
            }
        }
    }


    fun redirectAppSetting(fragment: Fragment) {
        AlertDialog.Builder(fragment.requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle("App Setting")
            .setMessage(
                rationale
                    ?: "ATMGO application required permission, Please grant permission from application setting."
            )
            .setCancelable(true)
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri: Uri = Uri.fromParts("package", fragment.requireContext().packageName, null)
                intent.data = uri
                fragment.startActivity(intent)
            }.show()
    }

    private fun displayRationale(fragment: Fragment) {
        AlertDialog.Builder(fragment.requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle("Grant Permission")
            .setMessage(
                rationale ?: "SevenPay application required permission, Please grant permission."
            )
            .setCancelable(false)
            .setPositiveButton("GRANT PERMISSION") { _, _ ->
                requestPermissions()
            }.show()
    }

    private fun areAllPermissionsGranted(fragment: Fragment) =
        requiredPermissions?.all { it.isGranted(fragment) }


    private fun requestPermissions() {
        permissionCheck?.launch(getPermissionList())
    }

    private fun shouldShowPermissionRationale(fragment: Fragment) =
        requiredPermissions.any { it.requiresRationale(fragment) }

    private fun sendPositiveResult() {
        sendResultAndCleanUp(getPermissionList().associate { it to true })
    }

    private fun getPermissionList() =
        requiredPermissions.flatMap { it.permission.toList() }.toTypedArray()

    private fun arePermissionsGranted(fragment: Fragment) =
        requiredPermissions.all {
            it.isGranted(fragment)
        }

    private fun sendResultAndCleanUp(grantResults: Map<String, Boolean>) {
        callback(grantResults.all { it.value })
        detailedCallback(grantResults.mapKeys { Permission.from(it.key) })
        cleanUp()
    }

    private fun cleanUp() {
        requiredPermissions.clear()
        rationale = null
        callback = {}
        detailedCallback = {}
    }
}

private fun Permission.requiresRationale(fragment: Fragment) =
    permission.any { fragment.shouldShowRequestPermissionRationale(it) }

private fun Permission.isGranted(fragment: Fragment) =
    permission.all { hasPermission(fragment, it) }


fun hasPermission(fragment: Fragment, permission: String) =
    ContextCompat.checkSelfPermission(
        fragment.requireContext(),
        permission
    ) == PackageManager.PERMISSION_GRANTED

