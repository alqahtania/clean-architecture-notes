package com.abdull.cleannotes.framework.presentation.common

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abdull.cleannotes.business.domain.model.NoteFactory
import com.abdull.cleannotes.business.interactors.notedetail.NoteDetailInteractors
import com.abdull.cleannotes.business.interactors.notelist.NoteListInteractors
import com.abdull.cleannotes.framework.presentation.notedetail.NoteDetailViewModel
import com.abdull.cleannotes.framework.presentation.notelist.NoteListViewModel
import com.abdull.cleannotes.framework.presentation.splash.NoteNetworkSyncManager
import com.abdull.cleannotes.framework.presentation.splash.SplashViewModel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Abdullah Alqahtani on 11/26/2020.
 */


@Singleton
class NoteViewModelFactory
@Inject
constructor(
    private val noteListInteractors: NoteListInteractors,
    private val noteDetailInteractors: NoteDetailInteractors,
    private val noteNetworkSyncManager: NoteNetworkSyncManager,
    private val noteFactory: NoteFactory,
    private val editor: SharedPreferences.Editor,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when(modelClass){

            NoteListViewModel::class.java -> {
                NoteListViewModel(
                    noteListInteractors = noteListInteractors,
                    noteFactory = noteFactory,
                    editor = editor,
                    sharedPreferences = sharedPreferences
                ) as T
            }

            NoteDetailViewModel::class.java -> {
                NoteDetailViewModel(
                    noteDetailInteractors = noteDetailInteractors
                ) as T
            }

            SplashViewModel::class.java -> {
                SplashViewModel(noteNetworkSyncManager) as T
            }

            else -> {
                throw IllegalArgumentException("unknown model class $modelClass")
            }
        }
    }
}