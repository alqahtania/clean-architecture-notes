package com.abdull.cleannotes.framework.datasource.cache.abstraction

import com.abdull.cleannotes.business.domain.model.Note
import com.abdull.cleannotes.framework.datasource.cache.database.NOTE_PAGINATION_PAGE_SIZE

/**
 * Created by Abdullah Alqahtani on 10/20/2020.
 */
interface NoteDaoService {

    suspend fun insertNote(note : Note) : Long

    suspend fun deleteNote(primaryKey : String) : Int

    suspend fun deleteNotes(notes : List<Note>) : Int

    suspend fun updateNote(
        primaryKey: String,
        newTitle : String,
        newBody : String,
        timestamp : String?
        ) : Int

    suspend fun searchNotes(): List<Note>

    suspend fun getAllNotes() : List<Note>

    suspend fun searchNotesOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun searchNotesOrderByDateASC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun searchNotesOrderByTitleDESC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun searchNotesOrderByTitleASC(
        query: String,
        page: Int,
        pageSize: Int = NOTE_PAGINATION_PAGE_SIZE
    ): List<Note>

    suspend fun searchNoteById(primaryKey: String) : Note?

    suspend fun getNumNotes() : Int

    //This is going to be used for testing only
    suspend fun insertNotes(notes : List<Note>) : LongArray

    suspend fun returnOrderedQuery(
        query: String,
        filterAndOrder: String,
        page: Int
    ): List<Note>

}