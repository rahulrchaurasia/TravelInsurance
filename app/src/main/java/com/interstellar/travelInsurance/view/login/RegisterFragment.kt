package com.interstellar.travelInsurance.view.login

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.interstellar.travelInsurance.BaseFragment
import com.interstellar.travelInsurance.R
import com.interstellar.travelInsurance.core.facade.SharedPreferenceManager
import com.interstellar.travelInsurance.core.viewmodel.register.RegistrationViewModel
import com.interstellar.travelInsurance.databinding.FragmentLoginBinding
import com.interstellar.travelInsurance.databinding.FragmentRegisterBinding
import com.interstellar.travelInsurance.event.FormEvent
import com.interstellar.travelInsurance.utils.Constant
import com.interstellar.travelInsurance.utils.ExtensionFun.toast
import com.interstellar.travelInsurance.utils.dateMask.applyDateMask
import com.interstellar.travelInsurance.utils.dateMask.parseDateString
import com.interstellar.travelInsurance.utils.hideKeyboard
import com.policyboss.demoandroidapp.Utility.ExtensionFun.dpToPx
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




        setupEventListeners()
       // New method for spinner setup
        setupOccupationSpinner()
        collectFlows() // <-- New method to collect Flows


        binding.tilOccupationType.error = "Please select an occupation"
    }


    private fun setupEventListeners() {


        binding.tieAddresse.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.editTextDateOfBirth.requestFocus()
                true
            } else {
                false
            }
        }

        // 👇 Perform Register action when "Done" pressed on Dropdown
        binding.autoCompleteOccupationType.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.btnRegister.performClick()
                true
            } else {
                false
            }
        }
        // Full Name Input
        binding.tieFullName.doAfterTextChanged {
            viewModel.updateFullName(it.toString())
        }

        // Mobile Number Input
        binding.tieMobile.doAfterTextChanged {
            viewModel.updateMobileNumber(it.toString())
        }


        // Address Input
        binding.tieAddresse.doAfterTextChanged {
            viewModel.updateAddress(it.toString())
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

//        binding.autoCompleteOccupationType.doAfterTextChanged {
//            val currentInput = it.toString()
//
//            viewModel.updateOccupationType(currentInput)
////            if (!viewModel.occupationOptions.contains(currentInput)) {
////                binding.tilOccupationType.error = "Please select a valid option"
////            }
//        }


    }




    private fun setupOccupationSpinner() {
        // Create the ArrayAdapter using your custom dropdown item layout
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item, // <--- Use your custom layout here!

            viewModel.occupationOptions
        )
        binding.autoCompleteTextView.setAdapter(adapter)
        val color = ContextCompat.getColor(requireContext(), R.color.grid_item_bg)


        val backgroundDrawable = MaterialShapeDrawable().apply {
            fillColor = ColorStateList.valueOf(color)

            shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setTopLeftCorner(CornerFamily.ROUNDED, 16.dpToPx())  // Only top-left
                .setTopRightCorner(CornerFamily.ROUNDED, 16.dpToPx()) // Only top-right
                // Bottom corners remain square (default)
                .build()
        }
        binding.autoCompleteTextView.setDropDownBackgroundDrawable(backgroundDrawable)

//        val drawable = ColorDrawable(color)
//        binding.autoCompleteTextView.setDropDownBackgroundDrawable(drawable)



        binding.autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            viewModel.updateOccupationType(selectedItem)
        }
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
                        binding.tvGenderError.visibility =
                            if (error.isNullOrBlank()) View.GONE else View.VISIBLE
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
                            else ->{

                                Log.d(Constant.TAG,"nothing")
                            }
                        }
                    }
                }
            }

        }



    }

    override fun onClick(view: View?) {
        requireContext().hideKeyboard(binding.root)
        when (view?.id) {

            binding.btnRegister.id -> {

                viewModel.submitForm() // Triggers local validation and API call (if any)
            }


        }
    }


}

