package com.abdull.cleannotes.di

import com.abdull.cleannotes.framework.presentation.BaseApplication
import com.abdull.cleannotes.framework.presentation.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Abdullah Alqahtani on 11/18/2020.
 */

@Singleton
@Component(
    modules = [
    AppModule::class,
    ProductionModule::class,
    NoteViewModelModule::class,
    NoteFragmentFactoryModule::class
    ]
)
interface AppComponent{

    @Component.Factory
    interface Factory{

        fun create(@BindsInstance app : BaseApplication) : AppComponent
    }

    fun inject(mainActivity: MainActivity)

}