package com.domberdev.zeitplan

import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import com.domberdev.zeitplan.profile.ui.GeneralSettingFragment

class PermissionRequester(
    activity: GeneralSettingFragment,
    private val permission: String,
    private val onRationale: () -> Unit = {},//Muestra el motivo de por que se ocupa este permiso
    private val onDenied: () -> Unit = {} //Muestra el mensaje de permiso rechazado
) {


    private var onGranted: () -> Unit = {}
    private val permissionLauncher =
        activity.registerForActivityResult(RequestPermission()) { isGranted ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when {
                    isGranted -> onGranted()
                    activity.shouldShowRequestPermissionRationale(permission) -> onRationale()
                    else -> onDenied()
                }
            } else {
                when {
                    isGranted -> onGranted()
                    else -> onDenied()
                }
            }
        }

    fun runWithPermission(body: () -> Unit) {
        onGranted = body
        permissionLauncher.launch(permission)
    }
}