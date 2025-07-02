package com.interstellar.travelInsurance.event

/**
 * Sealed class representing one-time UI events related to form interactions.
 * These events are typically consumed by the UI (Fragment/Activity) to trigger
 * a single action like showing a Toast, navigating, or displaying a dialog.
 */
sealed class FormEvent {
    data class Success(val message: String) : FormEvent()
    data class Error(val message: String) : FormEvent()
    // You could add other specific form events here if needed, e.g.:
    // object FormCleared : FormEvent()
    // data class NavigateTo(val destination: String) : FormEvent()
}