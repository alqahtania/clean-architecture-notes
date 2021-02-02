package com.abdull.cleannotes.business.interactors.splash

import com.abdull.cleannotes.business.data.cache.CacheResponseHandler
import com.abdull.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.abdull.cleannotes.business.data.network.ApiResponseHandler
import com.abdull.cleannotes.business.data.network.abstraction.NoteNetworkDataSource
import com.abdull.cleannotes.business.data.util.safeApiCall
import com.abdull.cleannotes.business.data.util.safeCacheCall
import com.abdull.cleannotes.business.domain.model.Note
import com.abdull.cleannotes.business.domain.state.DataState
import com.abdull.cleannotes.util.printLogD
import kotlinx.coroutines.Dispatchers.IO


class SyncDeletedNotes(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
) {


    suspend fun syncDeletedNotes(){

        val apiResult = safeApiCall(IO){
            noteNetworkDataSource.getDeletedNote()
        }

        val response = object : ApiResponseHandler<List<Note>, List<Note>>(
            response = apiResult,
            stateEvent = null
        ){
            override suspend fun handleSuccess(resultObject: List<Note>): DataState<List<Note>> {

                return DataState.data(
                    response = null,
                    data = resultObject,
                    stateEvent = null
                )
            }
        }.getResult()

        val notes = response?.data ?: ArrayList()

        val cacheResult = safeCacheCall(IO){
            noteCacheDataSource.deleteNotes(notes)
        }

        // for debugging purposes
        object : CacheResponseHandler<Int, Int>(
            response = cacheResult,
            stateEvent = null
        ){
            override fun handleSuccess(resultObject: Int): DataState<Int>? {
                printLogD("SyncDeletedNotes", "num deleted notes: $resultObject")
                return DataState.data(
                    response = null,
                    data = null,
                    stateEvent = null
                )
            }
        }
    }


}