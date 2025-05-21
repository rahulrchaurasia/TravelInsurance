package com.interstellar.travelInsurance.view.home


//Mark: Handle custom BackPress from child frag back handling  to stack navigation any Parent Fragment
interface ICustomBackNavigation {

    fun onCustomBackPressed(): Boolean
}