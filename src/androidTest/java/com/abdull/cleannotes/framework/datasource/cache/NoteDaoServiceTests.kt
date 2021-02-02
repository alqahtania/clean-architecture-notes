package com.abdull.cleannotes.framework.datasource.cache

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.abdull.cleannotes.business.domain.model.Note
import com.abdull.cleannotes.business.domain.util.DateUtil
import com.abdull.cleannotes.di.TestAppComponent
import com.abdull.cleannotes.framework.BaseTest
import com.abdull.cleannotes.framework.datasource.cache.abstraction.NoteDaoService
import com.abdull.cleannotes.framework.datasource.cache.database.NoteDao
import com.abdull.cleannotes.framework.datasource.cache.implementation.NoteDaoServiceImplementation
import com.abdull.cleannotes.framework.datasource.cache.mappers.CacheMapper
import com.abdull.cleannotes.framework.datasource.data.NoteDataFactory
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// runBlockingTest doesn't work:
// https://github.com/Kotlin/kotlinx.coroutines/issues/1204

/*
    LEGEND:
    1. CBS = "Confirm by searching"

    Test cases:
//    1. confirm database not empty to start (should be test data inserted from CacheTest.kt)
//    2. insert a new note, CBS
//    3. insert a list of notes, CBS
//    4. insert 1000 new notes, confirm filtered search query works correctly
//    5. insert 1000 new notes, confirm db size increased
//    6. delete new note, confirm deleted
//    7. delete list of notes, CBS
//    8. update a note, confirm updated
//    9. search notes, order by date (ASC), confirm order
//    10. search notes, order by date (DESC), confirm order
//    11. search notes, order by title (ASC), confirm order
//    12. search notes, order by title (DESC), confirm order
 */

@RunWith(AndroidJUnit4ClassRunner::class)
class NoteDaoServiceTests : BaseTest() {

    // system in test
    private val noteDaoService: NoteDaoService

    // dependencies
    @Inject
    lateinit var dao: NoteDao

    @Inject
    lateinit var noteDataFactory: NoteDataFactory

    @Inject
    lateinit var dateUtil: DateUtil

    @Inject
    lateinit var cacheMapper: CacheMapper

    init {
        injectTest()
        insertTestData()
        noteDaoService = NoteDaoServiceImplementation(
            noteDao = dao,
            noteMapper = cacheMapper,
            dateUtil = dateUtil
        )
    }


    private fun insertTestData() = runBlocking {

        val entityList = cacheMapper.noteListToEntityList(
            noteDataFactory.produceListOfNotes()
        )

        dao.insertNotes(entityList)
    }

    //    1. confirm database not empty to start (should be test data inserted from CacheTest.kt)
    @Test
    fun a_searchNotes_confirmDbNotEmpty() = runBlocking {

        val numNotes = noteDaoService.getNumNotes()

        assertTrue { numNotes > 0 }

    }


    //    2. insert a new note, CBS

    @Test
    fun insertNote_CBS() = runBlocking {

        val newNote = noteDataFactory.createSingleNote(
            id = null,
            title = "Super cool title",
            body = "Super cool body"
        )

        noteDaoService.insertNote(newNote)

        val searchNotes = noteDaoService.getAllNotes()

        assert(searchNotes.contains(newNote))

    }

    //    3. insert a list of notes, CBS
    @Test
    fun insertNoteList_CBS() = runBlocking {

        val noteList = noteDataFactory.createNoteList(10)

        noteDaoService.insertNotes(noteList)

        val queriedNotes = noteDaoService.getAllNotes()

        assert(queriedNotes.containsAll(noteList))

    }

    //    4. insert 1000 new notes, confirm filtered search query works correctly
    @Test
    fun insert1000Notes_searchNotesByTitle_confirm50ExpectedValues() = runBlocking {

        val noteList = noteDataFactory.createNoteList(1000)

        noteDaoService.insertNotes(noteList)

        repeat(50) {
            val randomIndex = Random.nextInt(0, noteList.size)
            val result = noteDaoService.searchNotesOrderByTitleASC(
                query = noteList.get(randomIndex).title,
                page = 1,
                pageSize = 1
            )
            assertEquals(
                noteList.get(randomIndex).title,
                result.get(0).title
            )
        }

    }

    //    5. insert 1000 new notes, confirm db size increased
    @Test
    fun insert1000Notes_confirmNumNotesInDb() = runBlocking {

        val currentNumNotes = noteDaoService.getNumNotes()

        val noteList = noteDataFactory.createNoteList(1000)

        noteDaoService.insertNotes(noteList)

        val numNotes = noteDaoService.getNumNotes()

        assertEquals(
            currentNumNotes + 1000,
            numNotes
        )

    }

    //    6. delete new note, confirm deleted
    @Test
    fun insertNote_deleteNote_confirmDeleted() = runBlocking {

        val newNote = noteDataFactory.createSingleNote(
            id = null,
            title = "something",
            body = "something more"
        )

        noteDaoService.insertNote(newNote)

        val searchNote = noteDaoService.searchNoteById(newNote.id)

        assert(searchNote == newNote)

        noteDaoService.deleteNote(newNote.id)

        val searchNoteAgain = noteDaoService.searchNoteById(newNote.id)

        assert(searchNoteAgain == null)


    }

    //    7. delete list of notes, CBS
    @Test
    fun deleteNoteList_confirmDeleted() = runBlocking {

        val noteList = ArrayList(noteDaoService.getAllNotes())

        val notesToDelete = ArrayList<Note>()

        var noteToDelete = noteList.get(Random.nextInt(0, noteList.size))
        noteList.remove(noteToDelete)
        notesToDelete.add(noteToDelete)

        noteToDelete = noteList.get(Random.nextInt(0, noteList.size))
        noteList.remove(noteToDelete)
        notesToDelete.add(noteToDelete)

        noteToDelete = noteList.get(Random.nextInt(0, noteList.size))
        noteList.remove(noteToDelete)
        notesToDelete.add(noteToDelete)

        noteToDelete = noteList.get(Random.nextInt(0, noteList.size))
        noteList.remove(noteToDelete)
        notesToDelete.add(noteToDelete)

        noteDaoService.deleteNotes(notesToDelete)

        val searchAllNotes = noteDaoService.getAllNotes()

        assertFalse(searchAllNotes.containsAll(notesToDelete))

    }
    //    8. update a note, confirm updated
    @Test
    fun insertNote_updateNote_confirmUpdated() = runBlocking {

        val newNote = noteDataFactory.createSingleNote(
            id = null,
            title = "something",
            body = "something more"
        )

        noteDaoService.insertNote(newNote)

        val newTitle = UUID.randomUUID().toString()
        val newBody = UUID.randomUUID().toString()

        noteDaoService.updateNote(
            primaryKey = newNote.id,
            newTitle = newTitle,
            newBody = newBody,
            timestamp = null
        )

        val notes = noteDaoService.getAllNotes()

        var foundNote = false

        for(note in notes){
            if(note.id.equals(newNote.id)){
                foundNote = true
                assertEquals(newNote.id, note.id)
                assertEquals(newTitle, note.title)
                assertEquals(newBody, note.body)
                assert(newNote.updated_at != note.updated_at)
                assertEquals(newNote.created_at, note.created_at)
                break

            }
        }

        assertTrue { foundNote }

    }
    //    9. search notes, order by date (ASC), confirm order
    @Test
    fun searchNotes_orderByDateASC_confirmOrder() = runBlocking {

        val noteList = noteDaoService.searchNotesOrderByDateASC(
            query = "",
            page = 1,
            pageSize = 100
        )

        // check that the date gets larger (newer) as iterate down the list
        var previousNoteDate = noteList.get(0).updated_at

        for(i in 1 until noteList.size){
            val currentNoteDate = noteList.get(i).updated_at
            assertTrue {
                currentNoteDate >= previousNoteDate
            }
            previousNoteDate = currentNoteDate
        }

    }
    //    10. search notes, order by date (DESC), confirm order

    @Test
    fun searchNotes_orderByDateDESC_confirmOrder() = runBlocking {
        val noteList = noteDaoService.searchNotesOrderByDateDESC(
            query = "",
            page = 1,
            pageSize = 100
        )

        // check that the date gets larger (newer) as iterate down the list
        var previous = noteList.get(0).updated_at
        for(index in 1..noteList.size - 1){
            val current = noteList.get(index).updated_at
            assertTrue { current <= previous }
            previous = current
        }
    }

    //    11. search notes, order by title (ASC), confirm order

    @Test
    fun searchNotes_orderByTitleASC_confirmOrder() = runBlocking {
        val noteList = noteDaoService.searchNotesOrderByTitleASC(
            query = "",
            page = 1,
            pageSize = 100
        )

        // check that the date gets larger (newer) as iterate down the list
        var previous = noteList.get(0).title
        for(index in 1..noteList.size - 1){
            val current = noteList.get(index).title

            assertTrue {
                listOf(previous, current)
                    .asSequence()
                    .zipWithNext { a, b ->
                        a <= b
                    }.all { it }
            }
            previous = current
        }
    }

    //    12. search notes, order by title (DESC), confirm order

    @Test
    fun searchNotes_orderByTitleDESC_confirmOrder() = runBlocking {
        val noteList = noteDaoService.searchNotesOrderByTitleDESC(
            query = "",
            page = 1,
            pageSize = 100
        )

        // check that the date gets larger (newer) as iterate down the list
        var previous = noteList.get(0).title
        for(index in 1..noteList.size - 1){
            val current = noteList.get(index).title

            assertTrue {
                listOf(previous, current)
                    .asSequence()
                    .zipWithNext { a, b ->
                        a >= b
                    }.all { it }
            }
            previous = current
        }
    }

    override fun injectTest() {
        (application.appComponent as TestAppComponent).inject(this)
    }
}