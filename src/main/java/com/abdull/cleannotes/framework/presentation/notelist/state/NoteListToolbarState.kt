package com.abdull.cleannotes.framework.presentation.notelist.state

/**
 * Created by Abdullah Alqahtani on 11/28/2020.
 */
sealed class NoteListToolbarState {

    class MultiSelectionState: NoteListToolbarState(){

        override fun toString(): String {
            return "MultiSelectionState"
        }
    }

    class SearchViewState: NoteListToolbarState(){

        override fun toString(): String {
            return "SearchViewState"
        }
    }
}
