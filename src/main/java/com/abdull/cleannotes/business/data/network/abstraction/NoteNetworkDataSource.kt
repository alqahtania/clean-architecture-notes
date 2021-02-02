package com.abdull.cleannotes.business.data.network.abstraction

import com.abdull.cleannotes.business.domain.model.Note

/**
 * Created by Abdullah Alqahtani on 10/19/2020.
 */

interface NoteNetworkDataSource{
    suspend fun insertOrUpdateNote(note : Note)

    suspend fun deleteNote(primaryKey : String)

    suspend fun insertDeletedNote(note : Note)

    suspend fun insertDeletedNotes(notes : List<Note>)

    suspend fun deleteDeletedNote(note : Note)

    suspend fun getDeletedNote() : List<Note>

    suspend fun deleteAllNotes()

    suspend fun searchNote(note: Note) : Note?

    suspend fun getAllNotes() : List<Note>

    suspend fun insertOrUpdateNotes(notes : List<Note>)

}