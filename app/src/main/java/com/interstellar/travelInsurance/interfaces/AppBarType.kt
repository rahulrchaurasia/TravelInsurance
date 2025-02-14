package com.interstellar.travelInsurance.interfaces

sealed class AppBarType {
    object Default : AppBarType()//layout : layout_default_toolbar
    object Custom1 : AppBarType() //layout : layout_custom_toolbar
    object Custom2 : AppBarType()
    object Hidden : AppBarType()
}