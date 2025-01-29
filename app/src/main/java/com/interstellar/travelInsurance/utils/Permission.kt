package com.interstellar.travelInsurance.utils

import android.Manifest
import android.Manifest.permission.*
import android.os.Build
import androidx.annotation.RequiresApi

sealed class Permission(vararg val permission: String) {


    object MandatoryForFeatureOne : Permission(WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION)

    object Location : Permission(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
    object Camera : Permission(CAMERA)

    object Storage : Permission(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    object MediaImage : Permission(READ_MEDIA_IMAGES)


    @RequiresApi(Build.VERSION_CODES.S)
    object Api12Group :
        Permission(
            READ_PHONE_STATE,
            WRITE_EXTERNAL_STORAGE,
            READ_EXTERNAL_STORAGE,
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
        )

    companion object {
        fun from(permission: String) = when (permission) {
            ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION -> Location
            WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE -> Storage
            CAMERA -> Camera

            READ_MEDIA_IMAGES -> MediaImage
            READ_PHONE_STATE, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE,
            ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION -> Api12Group

            else -> throw IllegalArgumentException("Unknown permission $permission")
        }
    }
}
