package com.abdull.cleannotes.business.interactors.notedetail

import com.abdull.cleannotes.business.interactors.common.DeleteNote
import com.abdull.cleannotes.framework.presentation.notedetail.state.NoteDetailViewState

/**
 * Created by Abdullah Alqahtani on 11/19/2020.
 */

class NoteDetailInteractors(
    val deleteNote : DeleteNote<NoteDetailViewState>,
    val updateNote: UpdateNote
)
