package com.interstellar.travelInsurance.core.viewmodel.shareViewModel

import androidx.lifecycle.ViewModel
import com.interstellar.travelInsurance.core.facade.SharedPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel  @Inject constructor(
    private val preferenceManager: SharedPreferenceManager
):ViewModel(){

    private val _userName = MutableStateFlow(preferenceManager.getUserName())
    val userName = _userName.asStateFlow()

    fun logout() {
        preferenceManager.clearData()
        _userName.value = "" // reflect immediately in UI
    }
}