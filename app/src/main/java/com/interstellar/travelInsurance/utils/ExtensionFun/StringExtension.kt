package com.policyboss.demoandroidapp.Utility.ExtensionFun

import com.interstellar.travelInsurance.utils.validator.DateValidator


fun String.isValidDate(format: String = "dd-MM-yyyy") = DateValidator(format).isValid(this)