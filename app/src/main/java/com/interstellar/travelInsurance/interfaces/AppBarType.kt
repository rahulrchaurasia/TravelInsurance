package com.interstellar.travelInsurance.interfaces

//sealed class AppBarType {
//    object Default : AppBarType()//layout : layout_default_toolbar
//    object Custom1 : AppBarType() //layout : layout_custom_toolbar
//    object Custom2 : AppBarType()
//    object Hidden : AppBarType()
//}

enum class AppBarType {
    DEFAULT,    // Standard toolbar with navigation
    CUSTOM,     // Custom layout (like home screen)
    NONE        // No toolbar (login, splash etc)
}

// Configuration class for AppBar settings
data class AppBarConfig(
    val title: String? = null,
    val showBack: Boolean = false,
    val customLayoutId: Int? = null
)
// Optional config class if needed
data class CustomHeaderConfig(
    val layoutResId: Int,
    val onBackPressed: (() -> Unit)? = null
)