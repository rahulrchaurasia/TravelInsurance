/*
* Copyright 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.interstellar.travelInsurance.utils.ExtensionFun

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner

import com.google.android.material.snackbar.Snackbar

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**************** For Snackbar *********************************************/
//fun View.showSnackbar(msgId: Int, length: Int) {
//    showSnackbar(context.getString(msgId), length)
//}
//
//fun View.showSnackbar(msg: String, length: Int) {
//    showSnackbar(msg, length, null, {})
//}
fun View.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).show()
}

fun View.showSnackbar(
        msgId: Int,
        length: Int,
        actionMessageId: Int,
        action: (View) -> Unit
) {
    showSnackbar(context.getString(msgId), length, context.getString(actionMessageId), action)
}

fun View.showSnackbar(
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit
) {
    val snackbar = Snackbar.make(this, msg, length)
    if (actionMessage != null) {
        snackbar.setAction(actionMessage) {
            action(this)
        }.show()
    }
}
//*********** Snackbar with Action ****************

//    layout.showSnackbar(
//    "Required for PolicyBoss Pro to get Location",
//    Snackbar.LENGTH_INDEFINITE,
//    "ALLOW"){
//
//       print("Action_
//
//    }
/**************** For AlertDialog with Action *********************************************/

 fun View.toast(context: Context ,text: String) = Toast.makeText( context, text, Toast.LENGTH_SHORT).show()


    // for use myView.delayOnLifeCycle(500L){...}
     fun View.delayOnLifeCycle(

         durationInMilliSec : Long,
         dispatches : CoroutineDispatcher = Dispatchers.Main,
         block: () -> Unit
     ) : Job? = findViewTreeLifecycleOwner()?.let { lifecycleOwner ->

         lifecycleOwner.lifecycle.coroutineScope.launch(dispatches){
             delay(durationInMilliSec)
             block
         }

     }



