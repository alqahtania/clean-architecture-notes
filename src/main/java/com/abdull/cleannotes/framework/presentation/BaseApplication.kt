package com.abdull.cleannotes.framework.presentation

import android.app.Application
import com.abdull.cleannotes.di.AppComponent
import com.abdull.cleannotes.di.DaggerAppComponent

open class BaseApplication : Application(){

    lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()
        initAppComponent()
    }


    open fun initAppComponent(){

        appComponent = DaggerAppComponent.factory().create(this)
    }
}