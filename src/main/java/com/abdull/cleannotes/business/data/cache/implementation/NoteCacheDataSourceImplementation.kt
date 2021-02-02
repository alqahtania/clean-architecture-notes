package com.abdull.cleannotes.business.data.cache.implementation

import com.abdull.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.abdull.cleannotes.business.domain.model.Note
import com.abdull.cleannotes.framework.datasource.cache.abstraction.NoteDaoService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Abdullah Alqahtani on 10/19/2020.
 */

@Singleton
class NoteCacheDataSourceImplementation
@Inject
constructor(
    private val noteDaoService : NoteDaoService
) : NoteCacheDataSource{

    override suspend fun insertNote(note: Note)
            = noteDaoService.insertNote(note)

    override suspend fun deleteNote(primaryKey: String)
            = noteDaoService.deleteNote(primaryKey)

    override suspend fun deleteNotes(notes: List<Note>)
            = noteDaoService.deleteNotes(notes)

    override suspend fun updateNote(
        primaryKey: String,
        newTitle: String,
        newBody: String,
        timestamp : String?
        )
            = noteDaoService.updateNote(
        primaryKey,
        newTitle,
        newBody,
        timestamp
        )

    override suspend fun searchNotes(query: String, filterAndOrder: String, page: Int) : List<Note>{
        return noteDaoService.returnOrderedQuery(
            query = query,
            filterAndOrder = filterAndOrder,
            page = page
        )
    }

    override suspend fun getAllNotes(): List<Note> {
        return noteDaoService.getAllNotes()
    }

    override suspend fun searchNoteById(primaryKey: String)
            = noteDaoService.searchNoteById(primaryKey)

    override suspend fun getNumNotes()
            = noteDaoService.getNumNotes()

    override suspend fun insertNotes(notes: List<Note>)
    = noteDaoService.insertNotes(notes)
}