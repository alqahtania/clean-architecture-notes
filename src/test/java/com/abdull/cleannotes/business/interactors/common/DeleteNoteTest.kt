package com.abdull.cleannotes.business.interactors.common

import com.abdull.cleannotes.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.abdull.cleannotes.business.data.cache.FORCE_DELETE_NOTE_EXCEPTION
import com.abdull.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.abdull.cleannotes.business.data.network.abstraction.NoteNetworkDataSource
import com.abdull.cleannotes.business.domain.model.Note
import com.abdull.cleannotes.business.domain.model.NoteFactory
import com.abdull.cleannotes.di.DependencyContainer
import com.abdull.cleannotes.framework.presentation.notelist.state.NoteListStateEvent
import com.abdull.cleannotes.framework.presentation.notelist.state.NoteListViewState
import com.abdull.cleannotes.util.printLogD
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

/**
 * my use case
 * 1- deleteNoteFromCache_success_deleteFromNotesInNetworkAndInsertIntoDeletedNotesInNetwork
 *  a) search for a note in the cache and assert that note was retrieved
 *  b) delete that note from the cache and assert that note was deleted
 *  c) assert note was deleted from the network and assert that it was inserted into the deletes node
 *
 *  mitch's use cases
 *1. deleteNote_success_confirmNetworkUpdated()
a) delete a note
b) check for success message from flow emission
c) confirm note was deleted from "notes" node in network
d) confirm note was added to "deletes" node in network
2. deleteNote_fail_confirmNetworkUnchanged()
a) attempt to delete a note, fail since does not exist
b) check for failure message from flow emission
c) confirm network was not changed
3. throwException_checkGenericError_confirmNetworkUnchanged()
a) attempt to delete a note, force an exception to throw
b) check for failure message from flow emission
c) confirm network was not changed
 *
 *  */


class DeleteNoteTest {


    //system in test
    private val deleteNote: DeleteNote<NoteListViewState>

    //dependencies
    private val dependencyContainer: DependencyContainer
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteNetworkDataSource: NoteNetworkDataSource
    private val noteFactory: NoteFactory

    init {
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteNetworkDataSource = dependencyContainer.noteNetworkDataSource
        noteFactory = dependencyContainer.noteFactory
        deleteNote = DeleteNote(
            noteCacheDataSource,
            noteNetworkDataSource
        )
    }

    @Test
    fun deleteNoteFromCache_success_deleteFromNotesInNetworkAndInsertIntoDeletedNotesInNetwork() =
        runBlocking {

            val getNoteFromCache: Note? =
                noteCacheDataSource.searchNoteById("2474abaa-788a-4a6b-948z-87a2167hb0ec")

            assertTrue(getNoteFromCache != null)

            getNoteFromCache?.let {
                deleteNote.deleteNote(
                    note = it,
                    stateEvent = NoteListStateEvent.DeleteNoteEvent(it)
                ).collect {
                    assertEquals(
                        it?.stateMessage?.response?.message,
                        DeleteNote.DELETE_NOTE_SUCCESS
                    )
                }
            }

            val getSameNoteFromCache: Note? =
                noteCacheDataSource.searchNoteById("2474abaa-788a-4a6b-948z-87a2167hb0ec")

            assertTrue(getSameNoteFromCache == null)


            val getTheNoteFromTheNetwork = noteNetworkDataSource.searchNote(getNoteFromCache!!)

            assertTrue(getTheNoteFromTheNetwork == null)

            val getTheNoteFromTheDeletes = noteNetworkDataSource.getDeletedNote()

            assertTrue(getTheNoteFromTheDeletes.contains(getNoteFromCache))


        }


    @Test
    fun deleteNote_success_confirmNetworkUpdated() = runBlocking {
        val noteToDelete = noteCacheDataSource.searchNotes(
            query = "",
            filterAndOrder = "",
            page = 1
        ).get(0)

        printLogD("DeleteNoteTest", "n")
        deleteNote.deleteNote(
            note = noteToDelete,
            stateEvent = NoteListStateEvent.DeleteNoteEvent(noteToDelete)
        ).collect {
            assertEquals(
                it?.stateMessage?.response?.message,
                DeleteNote.DELETE_NOTE_SUCCESS
            )
        }

        //confirm was deleted from 'notes' node
        val wasNoteDeleted = !noteNetworkDataSource.getAllNotes()
            .contains(noteToDelete)
        assertTrue(wasNoteDeleted)

        //confirm was inserted into 'deletes' node
        val wasDeletedNoteInserted = noteNetworkDataSource.getDeletedNote()
            .contains(noteToDelete)
        assertTrue(wasDeletedNoteInserted)

    }

    @Test
    fun deleteNote_fail_confirmNetworkUnchanged() = runBlocking {

        val noteToDelete = Note(
            id = UUID.randomUUID().toString(),
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString(),
            created_at = UUID.randomUUID().toString(),
            updated_at = UUID.randomUUID().toString()
        )

        deleteNote.deleteNote(
            note = noteToDelete,
            stateEvent = NoteListStateEvent.DeleteNoteEvent(noteToDelete)
        ).collect {
            assertEquals(
                it?.stateMessage?.response?.message,
                DeleteNote.DELETE_NOTE_FAILURE
            )
        }

        //confirm was deleted from 'notes' node
        val notes = noteNetworkDataSource.getAllNotes()
        val numNotesInCache = noteCacheDataSource.getNumNotes()

        assertTrue(notes.size == numNotesInCache)

        //confirm was not inserted into 'deletes' node
        val wasDeletedNoteInserted = !noteNetworkDataSource.getDeletedNote()
            .contains(noteToDelete)
        assertTrue(wasDeletedNoteInserted)

    }

    @Test
    fun throwException_checkGenericError_confirmNetworkUnchanged() = runBlocking {

        val noteToDelete = Note(
            id = FORCE_DELETE_NOTE_EXCEPTION,
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString(),
            created_at = UUID.randomUUID().toString(),
            updated_at = UUID.randomUUID().toString()
        )

        deleteNote.deleteNote(
            note = noteToDelete,
            stateEvent = NoteListStateEvent.DeleteNoteEvent(noteToDelete)
        ).collect {
            assert(
                it?.stateMessage?.response?.message?.contains(CACHE_ERROR_UNKNOWN) ?: false

            )
        }

        //confirm was deleted from 'notes' node
        val notes = noteNetworkDataSource.getAllNotes()
        val numNotesInCache = noteCacheDataSource.getNumNotes()

        assertTrue(notes.size == numNotesInCache)

        //confirm was not inserted into 'deletes' node
        val wasDeletedNoteInserted = !noteNetworkDataSource.getDeletedNote()
            .contains(noteToDelete)
        assertTrue(wasDeletedNoteInserted)

    }

}



