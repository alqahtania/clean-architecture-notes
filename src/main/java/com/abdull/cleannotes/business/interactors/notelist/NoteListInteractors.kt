package com.abdull.cleannotes.business.interactors.notelist

import com.abdull.cleannotes.business.interactors.common.DeleteNote
import com.abdull.cleannotes.framework.presentation.notelist.state.NoteListViewState

/**
 * Created by Abdullah Alqahtani on 11/19/2020.
 */

class NoteListInteractors (
    val insertNewNote: InsertNewNote,
    val deleteNote: DeleteNote<NoteListViewState>,
    val searchNotes: SearchNotes,
    val getNumNotes: GetNumNotes,
    val restoreDeletedNote: RestoreDeletedNote,
    val deleteMultipleNotes: DeleteMultipleNotes
)