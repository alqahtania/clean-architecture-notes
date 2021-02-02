package com.abdull.cleannotes.business.data.cache.abstraction

import com.abdull.cleannotes.business.domain.model.Note

/**
 * Created by Abdullah Alqahtani on 10/19/2020.
 */

interface NoteCacheDataSource{

    suspend fun insertNote(note : Note) : Long

    suspend fun deleteNote(primaryKey : String) : Int

    suspend fun deleteNotes(notes : List<Note>) : Int

    suspend fun updateNote(
        primaryKey: String,
        newTitle : String,
        newBody : String,
        timestamp : String?
        ) : Int

    suspend fun getAllNotes() : List<Note>

    suspend fun searchNotes(
        query : String,
        filterAndOrder : String,
        page : Int
    ) : List<Note>

    suspend fun searchNoteById(primaryKey: String) : Note?

    suspend fun getNumNotes() : Int

    //This is going to be used for testing only
    suspend fun insertNotes(notes : List<Note>) : LongArray
}