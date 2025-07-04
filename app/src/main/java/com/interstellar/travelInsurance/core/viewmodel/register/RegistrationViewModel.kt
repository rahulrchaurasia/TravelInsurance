package com.interstellar.travelInsurance.core.viewmodel.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
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
    // NEW: Date of Birth fields
    private val _dobRaw = MutableStateFlow("")
    val dobRaw: StateFlow<String> = _dobRaw.asStateFlow()

    private val _dobFormatted = MutableStateFlow("")
    val dobFormatted: StateFlow<String> = _dobFormatted.asStateFlow()

    private val _cursorPosition = MutableStateFlow(0)
    val cursorPosition: StateFlow<Int> = _cursorPosition.asStateFlow()
/////////////

    private val _dobError = MutableStateFlow<String?>(null)
    val dobError: StateFlow<String?> = _dobError.asStateFlow()

    // Gender
    private val _gender = MutableStateFlow<String?>(null) // "Male" or "Female"
    val gender: StateFlow<String?> = _gender.asStateFlow()
    private val _genderError = MutableStateFlow<String?>(null)
    val genderError: StateFlow<String?> = _genderError.asStateFlow()

    // Occupation Type
    private val _occupationType = MutableStateFlow("")
    val occupationType: StateFlow<String> = _occupationType.asStateFlow()
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



    // NEW: Date of Birth update method
    fun updateDOB(input: String, currentCursorPosition: Int = input.length) {
        val digits = input.filter { it.isDigit() }

        if (digits.length <= 8) {
            _dobRaw.value = digits
            val formattedDob = formatDOB(digits)
            _dobFormatted.value = formattedDob

            // Calculate new cursor position based on formatting
            val newCursorPosition = calculateCursorPosition(digits, currentCursorPosition, formattedDob)
            _cursorPosition.value = newCursorPosition

            _dobError.value = when {
                digits.isEmpty() -> "Date of birth is required"
                digits.length < 8 -> "Complete date is required"
                !isValidDOB(formattedDob) -> "Invalid date format"
                else -> null
            }
        }
    }

    private fun formatDOB(digits: String): String {
        return when {
            digits.length >= 5 -> "${digits.take(2)}-${digits.substring(2, 4)}-${digits.drop(4)}"
            digits.length >= 3 -> "${digits.take(2)}-${digits.drop(2)}"
            else -> digits
        }
    }

    private fun calculateCursorPosition(digits: String, oldCursorPos: Int, formattedText: String): Int {
        val digitsBeforeCursor = minOf(oldCursorPos, digits.length)

        return when {
            digitsBeforeCursor <= 2 -> digitsBeforeCursor
            digitsBeforeCursor <= 4 -> digitsBeforeCursor + 1
            else -> digitsBeforeCursor + 2
        }.coerceAtMost(formattedText.length)
    }

    private fun isValidDOB(dob: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return try {
            val parsedDate = LocalDate.parse(dob, formatter)
            val minDate = LocalDate.of(1900, 1, 1)
            val maxDate = LocalDate.now()

            !parsedDate.isBefore(minDate) && !parsedDate.isAfter(maxDate)
        } catch (e: DateTimeParseException) {
            false
        }
    }
    fun updateGender(selectedGender: String) {
        _gender.value = selectedGender
        validateGender(selectedGender)
    }

    fun updateOccupationType(selectedType: String) {
        _occupationType.value = selectedType
      //  _occupationTypeError.value = null // Clear error immediately on selection
        validateOccupationType()
    }

    // --- Validation Methods ---

    private fun validateFullName(name: String): Boolean {
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

    private fun validateMobileNumber(number: String): Boolean {
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

    private fun validateAddress(address: String): Boolean {
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


    fun validateOccupationType(): Boolean { // Returns Boolean
        return if (_occupationType.value.isBlank()) {
            _occupationTypeError.value = "Please select an occupation."
            false
        } else if (_occupationType.value !in occupationOptions) {
            _occupationTypeError.value = "Invalid occupation selected."
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
        viewModelScope.launch {
            // Clear all previous errors at the start of a new submission attempt
            _fullNameError.value = null
            _mobileNumberError.value = null
            _addressError.value = null
            _dobError.value = null
            _genderError.value = null
            _occupationTypeError.value = null
            // ... clear any other relevant error states

            // Perform sequential validation
            if (!validateFullName(_fullName.value)) {
                _formEvents.emit(FormEvent.Error("Please correct Full Name."))
                return@launch // Stop here if validation fails
            }

            if (!validateMobileNumber(_mobileNumber.value)) {
                _formEvents.emit(FormEvent.Error("Please correct Mobile Number."))
                return@launch // Stop here
            }

            if (!validateAddress(_address.value)) {
                _formEvents.emit(FormEvent.Error("Please correct Address."))
                return@launch // Stop here
            }

            // Assuming _dobUnmasked and _dobFormatted are your DOB states
            if (!(_dobRaw.value.length == 8 && isValidDOB(_dobFormatted.value))) {
                _formEvents.emit(FormEvent.Error("Please correct Date of Birth."))
                return@launch // Stop here
            }

            if (!validateGender(_gender.value)) {
                _formEvents.emit(FormEvent.Error("Please select Gender."))
                return@launch // Stop here
            }

            // For OccupationType, ensure it's cast correctly for validation

            if (!validateOccupationType()) {
                _formEvents.emit(FormEvent.Error("Please select Occupation Type."))
                return@launch // Stop here
            }

            // If we reach here, all validations passed
            val formData = mapOf(
                "fullName" to _fullName.value,
                "mobileNumber" to _mobileNumber.value,
                "address" to _address.value,
                "dob" to _dobFormatted.value, // Send formatted DOB to API usually
                "gender" to _gender.value,
                "occupationType" to _occupationType.value
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


        }
    }

    // Other sealed class definition for OccupationType dropdown data

    sealed class OccupationType {
        object Unselected : OccupationType()
        data class Selected(val type: String) : OccupationType()
        // You might add an ID for API calls: data class Selected(val id: String, val type: String) : OccupationType()
    }


}
