package com.interstellar.travelInsurance.interfaces

//interface IHandleAppBar {
//
//    fun hideAppBar()
//    fun showAppBar()
//
//    // u can do all using this only
//    fun setAppBar(appBarType: AppBarType)
//}

interface IHandleAppBar {

    //fun setAppBarState(type: AppBarType, config: AppBarConfig? = null)

    fun showDefaultAppBar(title: String? = null)
   // fun showCustomAppBar(layoutResId: Int)
    fun hideAppBar()
}




interface AppBarHandlerOld {
    fun setAppBarType(type: AppBarType)
    fun setAppBarTitle(title: String)
    fun setCustomAppBar(layoutResId: Int)
}