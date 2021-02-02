package com.abdull.cleannotes.business.interactors.notelist

import com.abdull.cleannotes.business.data.NoteDataFactory
import com.abdull.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.abdull.cleannotes.business.domain.model.NoteFactory
import com.abdull.cleannotes.di.DependencyContainer
import com.abdull.cleannotes.framework.presentation.notelist.state.NoteListStateEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * 1. getNumNotes_success_confirmCorrect()
 *  a) get the number of notes in cache
 *  b) listen for GET_NUM_NOTES_SUCCESS from flow emission
 *  c) compare with the number of notes in the fake data set
 */

class GetNumNotesTest {


    //System in test
    private val getNumNotes : GetNumNotes

    //dependencies
    private val dependencyContainer: DependencyContainer
    private val noteCacheDataSource: NoteCacheDataSource
    private val noteFactory: NoteFactory
    private val noteDataFactory : NoteDataFactory

    init {
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteFactory = dependencyContainer.noteFactory
        noteDataFactory = dependencyContainer.noteDataFactory
        getNumNotes = GetNumNotes(
            noteCacheDataSource
        )
    }


    @Test
    fun getNumNotes_success_confirmCorrect() = runBlocking{
        var numOfNotes = 0
        getNumNotes.getNumNotes(
            NoteListStateEvent.GetNumNotesInCacheEvent()
        ).collect {

            assertEquals(
                it?.stateMessage?.response?.message,
                GetNumNotes.GET_NUM_NOTES_SUCCESS
            )
            numOfNotes = it?.data?.numNotesInCache ?: -1
        }

        assertTrue(numOfNotes == 10)
        //query the fakeDataSourceImpl and assert it equals the numOfNotes
        val numOfNotesInCache = noteCacheDataSource.getNumNotes()
        assertTrue(numOfNotesInCache == numOfNotes)
    }


}