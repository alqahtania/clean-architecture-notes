package com.abdull.cleannotes.framework.datasource.network

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.abdull.cleannotes.business.domain.model.Note
import com.abdull.cleannotes.di.TestAppComponent
import com.abdull.cleannotes.framework.BaseTest
import com.abdull.cleannotes.framework.datasource.data.NoteDataFactory
import com.abdull.cleannotes.framework.datasource.network.abstraction.NoteFirestoreService
import com.abdull.cleannotes.framework.datasource.network.implementation.NoteFirestoreServiceImplementation
import com.abdull.cleannotes.framework.datasource.network.implementation.NoteFirestoreServiceImplementation.Companion.NOTES_COLLECTION
import com.abdull.cleannotes.framework.datasource.network.implementation.NoteFirestoreServiceImplementation.Companion.USER_ID
import com.abdull.cleannotes.framework.datasource.network.mappers.NetworkMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.Assert.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.random.Random

/*
LEGEND:
1. CBS = "Confirm by searching"

Test cases:
1. insert a single note, CBS
2. update a random note, CBS
3. insert a list of notes, CBS
4. delete a single note, CBS
5. insert a deleted note into "deletes" node, CBS
6. insert a list of deleted notes into "deletes" node, CBS
7. delete a 'deleted note' (note from "deletes" node). CBS
 */

@ExperimentalCoroutinesApi
@FlowPreview
@RunWith(AndroidJUnit4ClassRunner::class)
class NoteFirestoreServiceTests : BaseTest(){

    // system in test
    private lateinit var noteFirestoreService: NoteFirestoreService


    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var noteDataFactory: NoteDataFactory

    @Inject
    lateinit var networkMapper: NetworkMapper

    init {
        injectTest()
        signIn()
        insertTestData()
    }

    @Before
    fun before(){
        noteFirestoreService = NoteFirestoreServiceImplementation(
            firebaseAuth = firebaseAuth,
            firestore = firestore,
            networkMapper = networkMapper
        )
    }

    private fun signIn() = runBlocking {
        firebaseAuth.signInWithEmailAndPassword(
            EMAIL,
            PASSWORD
        ).await()
    }

    private fun insertTestData(){
        val entityList = networkMapper.noteListToEntityList(
            noteDataFactory.produceListOfNotes()
        )

        for(entity in entityList){
            firestore
                .collection(NOTES_COLLECTION)
                .document(USER_ID)
                .collection(NOTES_COLLECTION)
                .document(entity.id)
                .set(entity)
        }

    }

    @Test
    fun insertSingleNote_CBS() = runBlocking {

        val note = noteDataFactory.createSingleNote(
            id = UUID.randomUUID().toString(),
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString()
        )

        noteFirestoreService.insertOrUpdateNote(note)

        val searchResult = noteFirestoreService.searchNote(note)

        assertEquals(
            note,
            searchResult
        )

    }

    @Test
    fun updateRandomNote_CBS() = runBlocking {

        val searchResults = noteFirestoreService.getAllNotes()

        // choose a random note from the list to update

        val randomNote = searchResults.get(Random.nextInt(0, searchResults.size))
        val UPDATED_TITLE = UUID.randomUUID().toString()
        val UPDATED_BODY = UUID.randomUUID().toString()
        var updatedNote = noteDataFactory.createSingleNote(
            id = randomNote.id,
            title = UPDATED_TITLE,
            body = UPDATED_BODY
        )

        // make the update
        noteFirestoreService.insertOrUpdateNote(updatedNote)

        updatedNote = noteFirestoreService.searchNote(updatedNote)!!

        assertEquals(UPDATED_TITLE, updatedNote.title)
        assertEquals(UPDATED_BODY, updatedNote.body)

    }

    @Test
    fun insertNoteList_CBS() = runBlocking {

        val list = noteDataFactory.createNoteList(50)

        noteFirestoreService.insertOrUpdateNotes(list)

        val searchResults = noteFirestoreService.getAllNotes()

        assertTrue(searchResults.containsAll(list))

    }

    @Test
    fun deleteSingleNote_CBS() = runBlocking {

        val noteList = noteFirestoreService.getAllNotes()

        // choose a random one to delete
        val noteToDelete = noteList.get(Random.nextInt(0, noteList.size))

        noteFirestoreService.deleteNote(noteToDelete.id)

        // confirm it no longer exists in the 'notes' node
        val searchResults = noteFirestoreService.getAllNotes()

        assertFalse(searchResults.contains(noteToDelete))
    }

    @Test
    fun insertIntoDeletesNode_CBS() = runBlocking {

        val noteList = noteFirestoreService.getAllNotes()

        // choose one at random to insert into 'deletes' node
        val noteToDelete = noteList.get(Random.nextInt(0, noteList.size))

        noteFirestoreService.insertDeletedNote(noteToDelete)

        // confirm it is now in the 'deletes' node
        val searchResults = noteFirestoreService.getDeletedNote()

        assertTrue(searchResults.contains(noteToDelete))

    }


    @Test
    fun insertListIntoDeletesNode_CBS() = runBlocking {

        val noteList = ArrayList(noteFirestoreService.getAllNotes())

        // choose some random notes to add to 'deletes' node
        val notesToDelete = ArrayList<Note>()

        // 1st
        var noteToDelete = noteList.get(Random.nextInt(0, noteList.size))
        noteList.remove(noteToDelete)
        notesToDelete.add(noteToDelete)

        // 2nd
        noteToDelete = noteList.get(Random.nextInt(0, noteList.size))
        noteList.remove(noteToDelete)
        notesToDelete.add(noteToDelete)

        // 3rd
        noteToDelete = noteList.get(Random.nextInt(0, noteList.size))
        noteList.remove(noteToDelete)
        notesToDelete.add(noteToDelete)

        // 4th
        noteToDelete = noteList.get(Random.nextInt(0, noteList.size))
        noteList.remove(noteToDelete)
        notesToDelete.add(noteToDelete)

        // insert into the 'deletes' node
        noteFirestoreService.insertDeletedNotes(notesToDelete)

        // confirm they were inserted into the 'deletes' node
        val searchResults = noteFirestoreService.getDeletedNote()

        assertTrue(searchResults.containsAll(notesToDelete))

    }

    @Test
    fun deleteDeletedNote_CBS() = runBlocking {

        val note = noteDataFactory.createSingleNote(
            id = UUID.randomUUID().toString(),
            title = UUID.randomUUID().toString(),
            body = UUID.randomUUID().toString()
        )

        // insert into the 'deletes' node
        noteFirestoreService.insertDeletedNote(note)

        // confirm note is in the 'deletes' node
        var searchResults = noteFirestoreService.getDeletedNote()
        assertTrue(searchResults.contains(note))

        // delete from the 'deletes' note
        noteFirestoreService.deleteDeletedNote(note)

        // confirm note is no longer in the 'deletes' node
        searchResults = noteFirestoreService.getDeletedNote()
        assertFalse(searchResults.contains(note))

    }

    override fun injectTest() {
        (application.appComponent as TestAppComponent).inject(this)
    }

    companion object {
        const val EMAIL = "abdalqa6@gmail.com"
        const val PASSWORD = "765CLnotesFB%/_"
    }

}

