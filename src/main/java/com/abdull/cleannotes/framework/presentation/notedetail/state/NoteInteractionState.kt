package com.abdull.cleannotes.framework.presentation.notedetail.state

/**
 * Created by Abdullah Alqahtani on 12/1/2020.
 */
sealed class NoteInteractionState {

    class EditState: NoteInteractionState() {

        override fun toString(): String {
            return "EditState"
        }
    }

    class DefaultState: NoteInteractionState(){

        override fun toString(): String {
            return "DefaultState"
        }
    }
}