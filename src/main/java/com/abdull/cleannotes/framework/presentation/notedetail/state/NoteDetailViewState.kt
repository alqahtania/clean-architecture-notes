package com.abdull.cleannotes.framework.presentation.notedetail.state

import android.os.Parcelable
import com.abdull.cleannotes.business.domain.model.Note
import com.abdull.cleannotes.business.domain.state.ViewState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoteDetailViewState(

    var note: Note? = null,

    var isUpdatePending: Boolean? = null

) : Parcelable, ViewState

