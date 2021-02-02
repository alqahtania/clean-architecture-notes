package com.abdull.cleannotes.business.interactors.notelist

import com.abdull.cleannotes.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.abdull.cleannotes.business.data.cache.FORCE_GENERAL_FAILURE
import com.abdull.cleannotes.business.data.cache.FORCE_NEW_NOTE_EXCEPTION
import com.abdull.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.abdull.cleannotes.business.data.network.abstraction.NoteNetworkDataSource
import com.abdull.cleannotes.business.domain.model.NoteFactory
import com.abdull.cleannotes.di.DependencyContainer
import com.abdull.cleannotes.framework.presentation.notelist.state.NoteListStateEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*


/*
Test cases:
1. restoreNote_success_confirmCacheAndNetworkUpdated()
    a) create a new note and insert it into the "deleted" node of network
    b) restore that note
    c) Listen for success msg RESTORE_NOTE_SUCCESS from flow
    d) confirm note is in the cache
    e) confirm note is in the network "notes" node
    f) confirm note is not in the network "deletes" node
2. restoreNote_fail_confirmCacheAndNetworkUnchanged()
    a) create a new note and insert it into the "deleted" node of network
    b) restore that note (force a failure)
    c) Listen for success msg RESTORE_NOTE_FAILED from flow
    d) confirm note is not in the cache
    e) confirm note is not in the network "notes" node
    f) confirm note is in the network "deletes" node
3. throwException_checkGenericError_confirmNetworkAndCacheUnchanged()
    a) create a new note and insert it into the "deleted" node of network
    b) restore that note (force an exception)
    c) Listen for success msg CACHE_ERROR_UNKNOWN from flow
    d) confirm note is not in the cache
    e) confirm note is not in the network "notes" node
    f) confirm note is in the network "deletes" node
 */

class RestoreDeletedNoteTest {

    //system in test
    private val restoreDeletedNote : RestoreDeletedNote
    // dependencies
    private val dependencyContainer: DependencyContainer
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteNetworkDataSource: NoteNetworkDataSource
    private val noteFactory : NoteFactory

    init {
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteNetworkDataSource = dependencyContainer.noteNetworkDataSource
        noteFactory = dependencyContainer.noteFactory
        restoreDeletedNote = RestoreDeletedNote(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource
        )
    }

    @Test
    fun restoreNote_success_confirmCacheAndNetworkUpdated() = runBlocking {

        // create a new note and insert into the 'deletes' node
        val restoredNote = noteFactory.createSingleNote(
            id = UUID.randomUUID().toString(),
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString()
        )

        noteNetworkDataSource.insertDeletedNote(restoredNote)

        //conform that restoredNote is in the deletes node before restoration
        val deletedNotes = noteNetworkDataSource.getDeletedNote()
        assertTrue(deletedNotes.contains(restoredNote))

        restoreDeletedNote.restoreDeletedNote(
            note = restoredNote,
            stateEvent = NoteListStateEvent.RestoreDeletedNoteEvent(restoredNote)
        ).collect {
            assertEquals(
                it?.stateMessage?.response?.message,
                RestoreDeletedNote.RESTORE_NOTE_SUCCESS
            )
        }

        // confirm note is in the cache
        val restoredNoteInCache = noteCacheDataSource.searchNoteById(restoredNote.id)
        assertTrue(restoredNoteInCache == restoredNote)

        // confirm note is in the network "notes" node
        val networkNotes = noteNetworkDataSource.getAllNotes()
        assertTrue(networkNotes.contains(restoredNote))

        // confirm note is not in the network "deletes" node
        val deletedNotesInNetwork = noteNetworkDataSource.getDeletedNote()
        assertFalse(deletedNotesInNetwork.contains(restoredNote))

    }

    @Test
    fun restoreNote_fail_confirmCacheAndNetworkUnchanged() = runBlocking {

        // create a new note and insert into the 'deletes' node
        val restoredNote = noteFactory.createSingleNote(
            id = FORCE_GENERAL_FAILURE,
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString()
        )

        noteNetworkDataSource.insertDeletedNote(restoredNote)

        //conform that restoredNote is in the deletes node before restoration
        val deletedNotes = noteNetworkDataSource.getDeletedNote()
        assertTrue(deletedNotes.contains(restoredNote))

        restoreDeletedNote.restoreDeletedNote(
            note = restoredNote,
            stateEvent = NoteListStateEvent.RestoreDeletedNoteEvent(restoredNote)
        ).collect {
            assertEquals(
                it?.stateMessage?.response?.message,
                RestoreDeletedNote.RESTORE_NOTE_FAILED
            )
        }

        // confirm note is NOT in the cache
        val restoredNoteInCache = noteCacheDataSource.searchNoteById(restoredNote.id)
        assertFalse(restoredNoteInCache == restoredNote)

        // confirm note is NOT in the network "notes" node
        val networkNotes = noteNetworkDataSource.getAllNotes()
        assertFalse(networkNotes.contains(restoredNote))

        // confirm note is in the network "deletes" node
        val deletedNotesInNetwork = noteNetworkDataSource.getDeletedNote()
        assertTrue(deletedNotesInNetwork.contains(restoredNote))

    }


    @Test
    fun throwException_checkGenericError_confirmNetworkAndCacheUnchanged() = runBlocking {

        // create a new note and insert into the 'deletes' node
        val restoredNote = noteFactory.createSingleNote(
            id = FORCE_NEW_NOTE_EXCEPTION,
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString()
        )

        noteNetworkDataSource.insertDeletedNote(restoredNote)

        //conform that restoredNote is in the deletes node before restoration
        val deletedNotes = noteNetworkDataSource.getDeletedNote()
        assertTrue(deletedNotes.contains(restoredNote))

        restoreDeletedNote.restoreDeletedNote(
            note = restoredNote,
            stateEvent = NoteListStateEvent.RestoreDeletedNoteEvent(restoredNote)
        ).collect {
            assert(it?.stateMessage?.response?.message
                ?.contains(CACHE_ERROR_UNKNOWN) ?: false)
        }

        // confirm note is NOT in the cache
        val restoredNoteInCache = noteCacheDataSource.searchNoteById(restoredNote.id)
        assertFalse(restoredNoteInCache == restoredNote)

        // confirm note is NOT in the network "notes" node
        val networkNotes = noteNetworkDataSource.getAllNotes()
        assertFalse(networkNotes.contains(restoredNote))

        // confirm note is in the network "deletes" node
        val deletedNotesInNetwork = noteNetworkDataSource.getDeletedNote()
        assertTrue(deletedNotesInNetwork.contains(restoredNote))

    }

}

