package com.abdull.cleannotes.di

import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import com.abdull.cleannotes.business.domain.model.NoteFactory
import com.abdull.cleannotes.business.interactors.notedetail.NoteDetailInteractors
import com.abdull.cleannotes.business.interactors.notelist.NoteListInteractors
import com.abdull.cleannotes.framework.presentation.common.NoteViewModelFactory
import com.abdull.cleannotes.framework.presentation.splash.NoteNetworkSyncManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Abdullah Alqahtani on 11/26/2020.
 */

@Module
object NoteViewModelModule {

    @Singleton
    @JvmStatic
    @Provides
    fun provideNoteViewModelFactory(
        noteListInteractors: NoteListInteractors,
        noteDetailInteractors: NoteDetailInteractors,
        noteNetworkSyncManager: NoteNetworkSyncManager,
        noteFactory: NoteFactory,
        editor: SharedPreferences.Editor,
        sharedPreferences: SharedPreferences
    ): ViewModelProvider.Factory{
        return NoteViewModelFactory(
            noteListInteractors = noteListInteractors,
            noteDetailInteractors = noteDetailInteractors,
            noteNetworkSyncManager = noteNetworkSyncManager,
            noteFactory = noteFactory,
            editor = editor,
            sharedPreferences = sharedPreferences
        )
    }

}