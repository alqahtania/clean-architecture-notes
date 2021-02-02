package com.abdull.cleannotes.business.interactors.splash

import com.abdull.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.abdull.cleannotes.business.data.network.abstraction.NoteNetworkDataSource
import com.abdull.cleannotes.business.domain.model.Note
import com.abdull.cleannotes.business.domain.model.NoteFactory
import com.abdull.cleannotes.business.domain.util.DateUtil
import com.abdull.cleannotes.di.DependencyContainer
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.collections.ArrayList


/*
Test cases:
1. insertNetworkNotesIntoCache()
    a) insert a bunch of new notes into the cache
    b) perform the sync
    c) check to see that those notes were inserted into the network
2. insertCachedNotesIntoNetwork()
    a) insert a bunch of new notes into the network
    b) perform the sync
    c) check to see that those notes were inserted into the cache
3. checkCacheUpdateLogicSync()
    a) select some notes from the cache and update them
    b) perform sync
    c) confirm network reflects the updates
4. checkNetworkUpdateLogicSync()
    a) select some notes from the network and update them
    b) perform sync
    c) confirm cache reflects the updates
 */


class SyncNotesTest {


    // system in test
    private val syncNotes: SyncNotes

    // dependencies
    private val dependencyContainer: DependencyContainer
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteNetworkDataSource: NoteNetworkDataSource
    private val noteFactory: NoteFactory
    private val dateUtil : DateUtil


    init {
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteNetworkDataSource = dependencyContainer.noteNetworkDataSource
        noteFactory = dependencyContainer.noteFactory
        dateUtil = dependencyContainer.dateUtil
        syncNotes = SyncNotes(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource
        )
    }

    @Test
    fun doSuccessiveUpdatesOccur() = runBlocking {

        // update a single note with new timestamp
        val newDate = dateUtil.getCurrentTimestamp()
        val updatedNote = Note(
            id = noteNetworkDataSource.getAllNotes().get(0).id,
            title = noteNetworkDataSource.getAllNotes().get(0).title,
            body = noteNetworkDataSource.getAllNotes().get(0).body,
            created_at = noteNetworkDataSource.getAllNotes().get(0).created_at,
            updated_at = newDate
        )
        noteNetworkDataSource.insertOrUpdateNote(updatedNote)

        syncNotes.syncNotes()

        delay(1001)

        // simulate launch app again
        syncNotes.syncNotes()

        // confirm the date was not updated a second time
        val notes = noteNetworkDataSource.getAllNotes()
        for(note in notes){
            if(note.id.equals(updatedNote.id)){
                assertTrue { note.updated_at.equals(newDate) }
            }
        }
    }

    @Test
    fun checkUpdatedAtDates() = runBlocking {

        // update a single note with new timestamp
        val newDate = dateUtil.getCurrentTimestamp()
        val updatedNote = Note(
            id = noteNetworkDataSource.getAllNotes().get(0).id,
            title = noteNetworkDataSource.getAllNotes().get(0).title,
            body = noteNetworkDataSource.getAllNotes().get(0).body,
            created_at = noteNetworkDataSource.getAllNotes().get(0).created_at,
            updated_at = newDate
        )
        noteNetworkDataSource.insertOrUpdateNote(updatedNote)

//        for(note in noteNetworkDataSource.getAllNotes()){
//            println("date: ${note.updated_at}")
//        }
//        println("BREAK")

        syncNotes.syncNotes()

        // confirm only a single 'updated_at' date was updated
        val notes = noteNetworkDataSource.getAllNotes()
        for(note in notes){
            noteCacheDataSource.searchNoteById(note.id)?.let { n ->
                println("date: ${n.updated_at}")
                if(n.id.equals(updatedNote.id)){
                    assertTrue { n.updated_at.equals(newDate) }
                }
                else{
                    assertFalse { n.updated_at.equals(newDate) }
                }
            }
        }
    }

    @Test
    fun insertNetworkNotesIntoCache() = runBlocking {

        val newNotes = noteFactory.createNoteList(50)

        noteNetworkDataSource.insertOrUpdateNotes(newNotes)


        syncNotes.syncNotes()

        for (note in newNotes) {
            val cachedNote = noteCacheDataSource.searchNoteById(note.id)
            assertTrue(cachedNote != null)
        }

    }

    @Test
    fun insertCachedNotesIntoNetwork() = runBlocking {

        val newNotes = noteFactory.createNoteList(50)
        noteCacheDataSource.insertNotes(newNotes)

        syncNotes.syncNotes()

        for (note in newNotes) {
            val networkNote = noteNetworkDataSource.searchNote(note)
            assertTrue(networkNote != null)
        }

    }


    @Test
    fun checkCacheUpdateLogicSync() = runBlocking {

        val cachedNotes = noteCacheDataSource.searchNotes("", "", 1)

        val notesToUpdate: ArrayList<Note> = ArrayList()
        for (cachedNote in cachedNotes) {
            val updatedNote = noteFactory.createSingleNote(
                id = cachedNote.id,
                title = UUID.randomUUID().toString(),
                body = UUID.randomUUID().toString()
            )
            notesToUpdate.add(updatedNote)
            if (notesToUpdate.size > 4) {
                break
            }
        }

        noteCacheDataSource.insertNotes(notesToUpdate)

        syncNotes.syncNotes()

        for (note in notesToUpdate) {
            val networkNote = noteNetworkDataSource.searchNote(note)
            assertEquals(
                note.id, networkNote?.id
            )
            assertEquals(
                note.title, networkNote?.title
            )
            assertEquals(
                note.body, networkNote?.body
            )
            assertEquals(
                note.updated_at, networkNote?.updated_at
            )

        }
    }

    @Test
    fun checkNetworkUpdateLogicSync() = runBlocking {


        val networkNotes = noteNetworkDataSource.getAllNotes()

        val notesToUpdate: ArrayList<Note> = ArrayList()
        for (networkNote in networkNotes) {
            val updatedNote = noteFactory.createSingleNote(
                id = networkNote.id,
                title = UUID.randomUUID().toString(),
                body = UUID.randomUUID().toString()
            )
            notesToUpdate.add(updatedNote)
            if (notesToUpdate.size > 4) {
                break
            }
        }

        noteNetworkDataSource.insertOrUpdateNotes(notesToUpdate)

        syncNotes.syncNotes()

        for (note in notesToUpdate) {
            val cachedNote = noteCacheDataSource.searchNoteById(note.id)
            assertEquals(
                note.id, cachedNote?.id
            )
            assertEquals(
                note.title, cachedNote?.title
            )
            assertEquals(
                note.body, cachedNote?.body
            )
            assertEquals(
                note.updated_at, cachedNote?.updated_at
            )

        }


    }

}