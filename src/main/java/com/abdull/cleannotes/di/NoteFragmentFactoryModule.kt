package com.abdull.cleannotes.di

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.abdull.cleannotes.business.domain.util.DateUtil
import com.abdull.cleannotes.framework.presentation.common.NoteFragmentFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Abdullah Alqahtani on 11/26/2020.
 */

@Module
object NoteFragmentFactoryModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory,
        dateUtil: DateUtil
    ): FragmentFactory {
        return NoteFragmentFactory(
            viewModelFactory,
            dateUtil
        )
    }
}