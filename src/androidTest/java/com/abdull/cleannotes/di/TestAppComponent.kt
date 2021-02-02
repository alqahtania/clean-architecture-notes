package com.abdull.cleannotes.di

import com.abdull.cleannotes.framework.datasource.cache.NoteDaoServiceTests
import com.abdull.cleannotes.framework.datasource.network.NoteFirestoreServiceTests
import com.abdull.cleannotes.framework.presentation.TestBaseApplication
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Abdullah Alqahtani on 11/22/2020.
 */

@Singleton
@Component(

    modules = [
    AppModule::class,
    TestModule::class
    ]
)
interface TestAppComponent : AppComponent{

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance app : TestBaseApplication) : TestAppComponent
    }


    fun inject(noteFirestoreServiceTests: NoteFirestoreServiceTests)

    fun inject(noteDaoServiceTest: NoteDaoServiceTests)


}