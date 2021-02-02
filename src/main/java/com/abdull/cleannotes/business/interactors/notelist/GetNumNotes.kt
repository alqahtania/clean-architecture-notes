package com.abdull.cleannotes.business.interactors.notelist

import com.abdull.cleannotes.business.data.cache.CacheResponseHandler
import com.abdull.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.abdull.cleannotes.business.data.util.safeCacheCall
import com.abdull.cleannotes.business.domain.state.*
import com.abdull.cleannotes.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by Abdullah Alqahtani on 10/21/2020.
 */
class GetNumNotes(
    private val noteCacheDataSource: NoteCacheDataSource
) {

    fun getNumNotes(
        stateEvent: StateEvent
    ) : Flow<DataState<NoteListViewState>?>
            = flow{

        val cacheResult = safeCacheCall(IO){
            noteCacheDataSource.getNumNotes()
        }

        val response
                = object : CacheResponseHandler<NoteListViewState, Int>(
            response = cacheResult,
            stateEvent = stateEvent
        ){

            override fun handleSuccess(
                resultObject: Int
            ): DataState<NoteListViewState> {

                val viewState = NoteListViewState(
                    numNotesInCache = resultObject
                )
                return DataState.data(
                    response = Response(
                        message = GET_NUM_NOTES_SUCCESS,
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Success()
                    ),
                    data = viewState,
                    stateEvent = stateEvent
                )

            }
        }.getResult()

        emit(response)
    }


    companion object{

        val GET_NUM_NOTES_SUCCESS = "Successfully retrieved the number of notes from the cache."
        val GET_NUM_NOTES_FAILED = "Failed to get the number of notes from the cache."
    }
}