package com.abdull.cleannotes.framework.presentation

import com.abdull.cleannotes.di.DaggerTestAppComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@FlowPreview
@ExperimentalCoroutinesApi
class TestBaseApplication : BaseApplication(){

    override fun initAppComponent() {

        appComponent = DaggerTestAppComponent.factory().create(this)

    }
}