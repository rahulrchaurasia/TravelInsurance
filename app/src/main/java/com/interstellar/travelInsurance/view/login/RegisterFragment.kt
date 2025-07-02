package com.interstellar.travelInsurance.view.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.core.facade.SharedPreferenceManager
import com.interstellar.travelInsurance.core.viewmodel.register.RegistrationViewModel
import com.interstellar.travelInsurance.databinding.FragmentLoginBinding
import com.interstellar.travelInsurance.databinding.FragmentRegisterBinding
import com.interstellar.travelInsurance.event.FormEvent
import com.interstellar.travelInsurance.utils.ExtensionFun.toast
import com.interstellar.travelInsurance.utils.dateMask.applyDateMask
import com.interstellar.travelInsurance.utils.dateMask.parseDateString
import com.interstellar.travelInsurance.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) ,
    OnClickListener {

    @Inject
    lateinit var preferenceManager: SharedPreferenceManager

    private val viewModel : RegistrationViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnRegister.setOnClickListener(this)

        binding.editTextDateOfBirth.applyDateMask(
            separator = '-', // Specify the separator
            onFullDateEntered = { fullDate ->
                // This callback is triggered when the date reaches DD-MM-YYYY format
                Log.d("DateInput", "Full date entered: $fullDate")
                binding.textInputLayoutDateOfBirth.error = null // Clear any previous errors on completion
                // You can perform immediate validation here if needed
                val parsedDate = parseDateString(fullDate)
                if (parsedDate == null) {
                    binding.textInputLayoutDateOfBirth.error = "Invalid Date"
                }
            }
        )

        setupEventListeners()
        setupOccupationSpinner() // New method for spinner setup
        collectFlows() // <-- New method to collect Flows
    }


    private fun setupEventListeners() {
        // Full Name Input
        binding.tieFullName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateFullName(s.toString())
            }
        })

        // Mobile Number Input
        binding.tieMobile.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateMobileNumber(s.toString())
            }
        })

        // Address Input
        binding.tieAddresse.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateAddress(s.toString())
            }
        })

        // Date of Birth Input (MaskedTextInputEditText)
        binding.editTextDateOfBirth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val formattedValue = s.toString()
                val unmaskedValue = binding.editTextDateOfBirth.text.toString()
                viewModel.updateDOB(formattedValue, unmaskedValue)
            }
        })

        // Optional: Cursor lock at end for DOB on touch
        binding.editTextDateOfBirth.setOnTouchListener { v, _ ->
            val editText = v as? EditText
            editText?.setSelection(editText.text?.length ?: 0)
            false
        }

        // Gender RadioGroup
        binding.rgGender.setOnCheckedChangeListener { group, checkedId ->
            val selectedGender = when (checkedId) {
                R.id.rbMale -> "Male"
                R.id.rbFemale -> "Female"
                else -> null // Should not happen if one is always checked
            }
            selectedGender?.let { viewModel.updateGender(it) }
        }


    }

    private fun setupOccupationSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line, // Or use a custom item layout
            viewModel.occupationOptions
        )
        binding.autoCompleteOccupationType.setAdapter(adapter)

        // Listen for item selection
        binding.autoCompleteOccupationType.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            viewModel.updateOccupationType(selectedItem)
        }

        // Optional: TextWatcher if you want to validate as user types in the dropdown (less common for dropdowns)
        binding.autoCompleteOccupationType.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // This might be called when user types or when an item is selected programmatically.
                // It's usually better to rely on OnItemClickListener for dropdowns,
                // but you can add specific validation here if the user can type arbitrary text.
                viewModel.updateOccupationType(s.toString()) // Pass current text for validation
            }
        })
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Collect and set EditText text (to handle state restoration or initial values)
                launch {
                    viewModel.fullName.collectLatest { name ->
                        if (binding.tieFullName.text.toString() != name) {
                            binding.tieFullName.setText(name)
                        }
                    }
                }
                launch {
                    viewModel.mobileNumber.collectLatest { number ->
                        if (binding.tieMobile.text.toString() != number) {
                            binding.tieMobile.setText(number)
                        }
                    }
                }
                launch {
                    viewModel.address.collectLatest { address ->
                        if (binding.tieAddresse.text.toString() != address) {
                            binding.tieAddresse.setText(address)
                        }
                    }
                }
                launch {
                    viewModel.dobFormatted.collectLatest { dob ->
                        if (binding.editTextDateOfBirth.text.toString() != dob) {
                            binding.editTextDateOfBirth.setText(dob)
                        }
                    }
                }
                // Gender: Set checked radio button if ViewModel has a value
                launch {
                    viewModel.gender.collectLatest { gender ->
                        when (gender) {
                            "Male" -> binding.rgGender.check(R.id.rbMale)
                            "Female" -> binding.rgGender.check(R.id.rbFemale)
                            else -> binding.rgGender.clearCheck() // Or set a default
                        }
                    }
                }
                // Occupation Type: Set selected text in AutoCompleteTextView
                launch {
                    viewModel.occupationType.collectLatest { occupation ->
                        val text = (occupation as? RegistrationViewModel.OccupationType.Selected)?.type ?: ""
                       // val text  = occupation.
                        if (binding.autoCompleteOccupationType.text.toString() != text) {
                            binding.autoCompleteOccupationType.setText(text, false) // false to not show dropdown
                        }
                    }
                }


                // Collect and display errors
                launch {
                    viewModel.fullNameError.collectLatest { error ->
                        binding.tilFullName.error = error
                    }
                }
                launch {
                    viewModel.mobileNumberError.collectLatest { error ->
                        binding.tilMobile.error = error
                    }
                }
                launch {
                    viewModel.addressError.collectLatest { error ->
                        binding.tilAddress.error = error
                    }
                }
                launch {
                    viewModel.dobError.collectLatest { error ->
                        binding.textInputLayoutDateOfBirth.error = error
                    }
                }
                launch {
                    viewModel.genderError.collectLatest { error ->
                        // Display error for RadioGroup in the dedicated TextView
                        binding.tvGenderError.text = error
                        binding.tvGenderError.visibility = if (error.isNullOrBlank()) View.GONE else View.VISIBLE
                    }
                }
                launch {
                    viewModel.occupationTypeError.collectLatest { error ->
                        binding.tilOccupationType.error = error
                    }
                }

                // Collect one-time form events (e.g., submission result)
                launch {
                    viewModel.formEvents.collect { event -> // Use collect, not collectLatest for events
                        when (event) {
                            is FormEvent.Success -> {
                               // Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                                // TODO: Navigate to next screen or clear form fields


                                binding.root.toast(requireContext(), event.message)
                            }

                            is FormEvent.Error -> {

                                binding.root.toast(requireContext(), event.message)
                            }


                        }
                    }
                }
            }
        }
    }


    override fun onClick(v: View?) {
        requireContext().hideKeyboard(binding.root)
        when (view?.id) {

            binding.btnRegister.id -> {

                viewModel.submitForm() // Triggers local validation and API call (if any)
            }


        }
    }


}

