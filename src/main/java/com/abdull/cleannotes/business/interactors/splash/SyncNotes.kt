package com.abdull.cleannotes.business.interactors.splash

import com.abdull.cleannotes.business.data.cache.CacheResponseHandler
import com.abdull.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.abdull.cleannotes.business.data.network.ApiResponseHandler
import com.abdull.cleannotes.business.data.network.abstraction.NoteNetworkDataSource
import com.abdull.cleannotes.business.data.util.safeApiCall
import com.abdull.cleannotes.business.data.util.safeCacheCall
import com.abdull.cleannotes.business.domain.model.Note
import com.abdull.cleannotes.business.domain.state.DataState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

/**
 * Created by Abdullah Alqahtani on 10/21/2020.
 */
class SyncNotes(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
) {

    suspend fun syncNotes() {

        val cachedNotesList = getCachedNotes()
        val networkNotesList = getNetworkNotes()
        syncNetworkNotesWithCachedNotes(
            ArrayList(cachedNotesList),
            networkNotesList
        )
    }

    private suspend fun getCachedNotes(): List<Note> {

        val cacheResult = safeCacheCall(IO) {
            noteCacheDataSource.getAllNotes()
        }

        val response = object : CacheResponseHandler<List<Note>, List<Note>>(
            response = cacheResult,
            stateEvent = null
        ) {
            override fun handleSuccess(resultObject: List<Note>): DataState<List<Note>>? {

                return DataState.data(
                    response = null,
                    data = resultObject,
                    stateEvent = null
                )
            }
        }.getResult()

        return response?.data ?: ArrayList()

    }

    private suspend fun getNetworkNotes(): List<Note> {

        val networkResult = safeApiCall(IO) {
            noteNetworkDataSource.getAllNotes()
        }

        val response = object : ApiResponseHandler<List<Note>, List<Note>>(
            response = networkResult,
            stateEvent = null
        ) {
            override suspend fun handleSuccess(resultObject: List<Note>): DataState<List<Note>> {
                return DataState.data(
                    response = null,
                    data = resultObject,
                    stateEvent = null
                )
            }
        }.getResult()

        return response?.data ?: ArrayList()
    }

    // get all notes from network
    // if they do not exist in cache, insert them
    // if they do exist in cache, make sure they are up to date
    // while looping, remove notes from the cachedNotes list. If any remain, it means they
    // should be in the network but aren't. So insert them.

    private suspend fun syncNetworkNotesWithCachedNotes(
        cachedNotes: ArrayList<Note>,
        networkNotes: List<Note>
    ) = withContext(IO) {


        for (note in networkNotes) {
            noteCacheDataSource.searchNoteById(note.id)?.let {
                cachedNotes.remove(it)
                checkIfCachedNoteRequiresUpdate(it, note)
            } ?: noteCacheDataSource.insertNote(note)
        }


        // insert any remaining notes in the cache into the network
        for (cachedNote in cachedNotes) {
            safeApiCall(IO) {
                noteNetworkDataSource.insertOrUpdateNote(cachedNote)
            }

        }
    }

    private suspend fun checkIfCachedNoteRequiresUpdate(cachedNote: Note, networkNote: Note) {
        val cacheUpdatedAt = cachedNote.updated_at
        val networkUpdatedAt = networkNote.updated_at

        if (networkUpdatedAt > cacheUpdatedAt) {
            safeCacheCall(IO) {
                noteCacheDataSource.updateNote(
                    primaryKey = networkNote.id,
                    newTitle = networkNote.title,
                    newBody = networkNote.body,
                    timestamp = networkNote.updated_at
                )
            }
        } else if(networkUpdatedAt < cacheUpdatedAt){
            safeApiCall(IO) {
                noteNetworkDataSource.insertOrUpdateNote(cachedNote)
            }
        }
    }
}