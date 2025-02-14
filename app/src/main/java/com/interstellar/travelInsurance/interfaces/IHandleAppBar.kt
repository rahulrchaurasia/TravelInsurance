package com.interstellar.travelInsurance.interfaces

interface IHandleAppBar {

    fun hideAppBar()
    fun showAppBar()

    // u can do all using this only
    fun setAppBar(appBarType: AppBarType)
}

interface IHandleToolbar {

    fun hideToolbar()
    fun showToolbar()
}

