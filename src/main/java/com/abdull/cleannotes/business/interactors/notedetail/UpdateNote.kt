package com.abdull.cleannotes.business.interactors.notedetail

import com.abdull.cleannotes.business.data.cache.CacheResponseHandler
import com.abdull.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.abdull.cleannotes.business.data.network.abstraction.NoteNetworkDataSource
import com.abdull.cleannotes.business.data.util.safeApiCall
import com.abdull.cleannotes.business.data.util.safeCacheCall
import com.abdull.cleannotes.business.domain.model.Note
import com.abdull.cleannotes.business.domain.state.*
import com.abdull.cleannotes.framework.presentation.notedetail.state.NoteDetailViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by Abdullah Alqahtani on 10/21/2020.
 */
class UpdateNote(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
) {


    fun updateNote(
        note: Note,
        stateEvent: StateEvent
    ): Flow<DataState<NoteDetailViewState>?> = flow {

        val cacheResult = safeCacheCall(IO) {
            noteCacheDataSource.updateNote(
                primaryKey = note.id,
                newTitle = note.title,
                newBody = note.body,
                timestamp = null
            )
        }

        val response =
            object : CacheResponseHandler<NoteDetailViewState, Int>(
                response = cacheResult,
                stateEvent = stateEvent
            ) {
                override fun handleSuccess(resultObject: Int): DataState<NoteDetailViewState>? {
                    return if(resultObject > 0){
                        DataState.data(
                            response = Response(
                                message = UPDATE_NOTE_SUCCESS,
                                uiComponentType = UIComponentType.Toast(),
                                messageType = MessageType.Success()
                            ),
                            data = null,
                            stateEvent = stateEvent
                        )
                    }else{
                        DataState.data(
                            response = Response(
                                message = UPDATE_NOTE_FAILED,
                                uiComponentType = UIComponentType.Toast(),
                                messageType = MessageType.Error()
                            ),
                            data = null,
                            stateEvent = stateEvent
                        )
                    }
                }
            }.getResult()

        emit(response)

        updateNetwork(
            response = response?.stateMessage?.response?.message,
            note = note
        )

    }

    private suspend fun updateNetwork(response : String?, note: Note){
        if(response.equals(UPDATE_NOTE_SUCCESS)){

            safeApiCall(IO){
                noteNetworkDataSource.insertOrUpdateNote(note)
            }
        }
    }

    companion object{

        val UPDATE_NOTE_SUCCESS = "Successfully updated note."
        val UPDATE_NOTE_FAILED = "Failed to update note."
        val UPDATE_NOTE_FAILED_PK = "Update failed. Note is missing primary key."
    }


}