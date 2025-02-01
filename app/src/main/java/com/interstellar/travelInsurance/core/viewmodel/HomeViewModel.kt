package com.interstellar.travelInsurance.core.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Config
import com.interstellar.travelInsurance.core.facade.SharedPreferenceManager
import com.interstellar.travelInsurance.utils.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferenceManager: SharedPreferenceManager
): ViewModel() {

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    fun  getData(){

        _isLoading.value = false
        viewModelScope.launch {

            try{

                delay(2000)
                _isLoading.value = false
                // Any logout API calls can go here

            }catch (ex: Exception) {
                // Handle error
                Log.d(Constant.TAG, ex.message.toString())
                _isLoading.value = false
            } finally {
                _isLoading.value = false
            }
        }

    }

    fun  logout(){

        viewModelScope.launch {

            try{

                _isLoading.value = true
                // Any logout API calls can go here
                preferenceManager.clearData()
                _logoutEvent.emit(Unit)
            }catch (ex: Exception) {
                // Handle error
                Log.d(Constant.TAG, ex.message.toString())
            } finally {
                _isLoading.value = false
            }
        }

    }
}