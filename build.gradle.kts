// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
//    dependencies {
//        classpath(libs.androidx.navigation.safe.args)
//        classpath(libs.hilt.android.gradle.plugin)
//    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false

    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.navigation.safeargs) apply false
}

//tasks.register("clean", Delete::class) {
//    delete(rootProject.buildDir)
//}