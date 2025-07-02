package com.interstellar.travelInsurance.core.viewmodel.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interstellar.travelInsurance.event.FormEvent
import com.interstellar.travelInsurance.utils.validator.DateValidatorUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegistrationViewModel @Inject constructor() : ViewModel() {


    // --- UI State (StateFlow) ---
//    private val _uiState = MutableStateFlow(RegistrationUiState())
//    val uiState: StateFlow<RegistrationUiState> = _uiState

    // Full Name
    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()
    private val _fullNameError = MutableStateFlow<String?>(null)
    val fullNameError: StateFlow<String?> = _fullNameError.asStateFlow()

    // Mobile Number
    private val _mobileNumber = MutableStateFlow("")
    val mobileNumber: StateFlow<String> = _mobileNumber.asStateFlow()
    private val _mobileNumberError = MutableStateFlow<String?>(null)
    val mobileNumberError: StateFlow<String?> = _mobileNumberError.asStateFlow()

    // Address
    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address.asStateFlow()
    private val _addressError = MutableStateFlow<String?>(null)
    val addressError: StateFlow<String?> = _addressError.asStateFlow()

    // Date of Birth
    private val _dobFormatted = MutableStateFlow("")
    val dobFormatted: StateFlow<String> = _dobFormatted.asStateFlow()
    private val _dobUnmasked = MutableStateFlow("")
    val dobUnmasked: StateFlow<String> = _dobUnmasked.asStateFlow()
    private val _dobError = MutableStateFlow<String?>(null)
    val dobError: StateFlow<String?> = _dobError.asStateFlow()

    // Gender
    private val _gender = MutableStateFlow<String?>(null) // "Male" or "Female"
    val gender: StateFlow<String?> = _gender.asStateFlow()
    private val _genderError = MutableStateFlow<String?>(null)
    val genderError: StateFlow<String?> = _genderError.asStateFlow()

    // Occupation Type
    private val _occupationType = MutableStateFlow<OccupationType>(OccupationType.Unselected)
    val occupationType: StateFlow<OccupationType> = _occupationType.asStateFlow()
    private val _occupationTypeError = MutableStateFlow<String?>(null)
    val occupationTypeError: StateFlow<String?> = _occupationTypeError.asStateFlow()

    // --- One-time Events (SharedFlow) ---
    private val _formEvents = MutableSharedFlow<FormEvent>()
    val formEvents: SharedFlow<FormEvent> = _formEvents.asSharedFlow()

    // --- Data for Spinner/Dropdown ---
    val occupationOptions = listOf("Student", "Employed", "Self-Employed", "Unemployed", "Retired")


    // --- Update Methods for UI Inputs ---

    fun updateFullName(input: String) {
        _fullName.value = input
        validateFullName(input)
    }

    fun updateMobileNumber(input: String) {
        _mobileNumber.value = input
        validateMobileNumber(input)
    }

    fun updateAddress(input: String) {
        _address.value = input
        validateAddress(input)
    }

    fun updateDOB(formattedValue: String, unmaskedValue: String) {
        _dobFormatted.value = formattedValue
        _dobUnmasked.value = unmaskedValue
        val isMaskFilled = formattedValue.length == 10
        validateDOB(unmaskedValue, formattedValue, isMaskFilled)
    }

    fun updateGender(selectedGender: String) {
        _gender.value = selectedGender
        validateGender(selectedGender)
    }

    fun updateOccupationType(selectedType: String) {
        _occupationType.value = OccupationType.Selected(selectedType)
        validateOccupationType(selectedType)
    }

    // --- Validation Methods ---

    fun validateFullName(name: String): Boolean {
        return if (name.isBlank()) {
            _fullNameError.value = "User Name is required"
            false
        } else if (!name.matches(Regex("^[a-zA-Z ]+\$"))) {
            _fullNameError.value = "User Name can only contain letters and spaces"
            false
        } else {
            _fullNameError.value = null
            true
        }
    }

    fun validateMobileNumber(number: String): Boolean {
        return if (number.isBlank()) {
            _mobileNumberError.value = "Mobile Number is required"
            false
        } else if (number.length != 10) {
            _mobileNumberError.value = "Mobile Number must be 10 digits"
            false
        } else if (!number.matches(Regex("^\\d{10}\$"))) {
            _mobileNumberError.value = "Invalid mobile number format"
            false
        } else {
            _mobileNumberError.value = null
            true
        }
    }

    fun validateAddress(address: String): Boolean {
        return if (address.isBlank()) {
            _addressError.value = "Address is required"
            false
        } else if (address.length < 10) {
            _addressError.value = "Address is too short"
            false
        } else {
            _addressError.value = null
            true
        }
    }

    fun validateDOB(unmaskedValue: String, formattedValue: String, isMaskFilled: Boolean): Boolean {
        if (unmaskedValue.isEmpty()) {
            _dobError.value = "Date of birth is required"
            return false
        }
        if (!isMaskFilled) {
            _dobError.value = "Complete date is required (DD-MM-YYYY)"
            return false
        }
        if (!DateValidatorUtils.isValidDateContent(formattedValue)) {
            _dobError.value = "Invalid date or out of range (1900-current)"
            return false
        }
        _dobError.value = null
        return true
    }

    fun validateGender(gender: String?): Boolean {
        return if (gender.isNullOrBlank()) {
            _genderError.value = "Gender is required"
            false
        } else {
            _genderError.value = null
            true
        }
    }

    fun validateOccupationType(occupation: String?): Boolean {
        return if (occupation.isNullOrBlank() || occupation == OccupationType.Unselected.toString()) {
            _occupationTypeError.value = "Occupation Type is required"
            false
        } else {
            _occupationTypeError.value = null
            true
        }
    }

    /**
     * Performs a full form validation, typically called on button click.
     * Emits a FormEvent (Success/Error) based on validation result.
     */
    fun submitForm() {
        val isFullNameValid = validateFullName(_fullName.value)
        val isMobileValid = validateMobileNumber(_mobileNumber.value)
        val isAddressValid = validateAddress(_address.value)
        val isDobValid = validateDOB(_dobUnmasked.value, _dobFormatted.value, _dobFormatted.value.length == 10)
        val isGenderValid = validateGender(_gender.value)
        val isOccupationValid = validateOccupationType((_occupationType.value as? OccupationType.Selected)?.type)

        val isFormValid = isFullNameValid && isMobileValid && isAddressValid && isDobValid && isGenderValid && isOccupationValid

        viewModelScope.launch {
            if (isFormValid) {
                // If form is valid, prepare data for API call or further processing
                val formData = mapOf(
                    "fullName" to _fullName.value,
                    "mobileNumber" to _mobileNumber.value,
                    "address" to _address.value,
                    "dob" to _dobFormatted.value, // Send formatted DOB to API usually
                    "gender" to _gender.value,
                    "occupationType" to (_occupationType.value as? OccupationType.Selected)?.type
                )
                println("Form Data: $formData")

                // TODO: Make your actual API call here
                // Example:
                // when (val result = yourRepository.submitRegistration(formData)) {
                //     is ApiResult.Loading -> _formEvents.emit(FormEvent.Info("Submitting..."))
                //     is ApiResult.Success -> _formEvents.emit(FormEvent.Success("Registration successful!"))
                //     is ApiResult.Error -> _formEvents.emit(FormEvent.Error("Submission failed: ${result.exception.message}"))
                // }

                _formEvents.emit(FormEvent.Success("Form Submitted Successfully!")) // Simulate success
            } else {
                _formEvents.emit(FormEvent.Error("Please correct the errors."))
            }
        }
    }



    // Other sealed class definition for OccupationType dropdown data

    sealed class OccupationType {
        object Unselected : OccupationType()
        data class Selected(val type: String) : OccupationType()
        // You might add an ID for API calls: data class Selected(val id: String, val type: String) : OccupationType()
    }


}
