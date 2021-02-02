package com.abdull.cleannotes.business.interactors.common

import com.abdull.cleannotes.business.data.cache.CacheResponseHandler
import com.abdull.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.abdull.cleannotes.business.data.network.abstraction.NoteNetworkDataSource
import com.abdull.cleannotes.business.data.util.safeApiCall
import com.abdull.cleannotes.business.data.util.safeCacheCall
import com.abdull.cleannotes.business.domain.model.Note
import com.abdull.cleannotes.business.domain.state.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by Abdullah Alqahtani on 10/21/2020.
 */
class   DeleteNote<ViewState : com.abdull.cleannotes.business.domain.state.ViewState>(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource
) {


    fun deleteNote(
        note : Note,
        stateEvent : StateEvent
    ) : Flow<DataState<ViewState>?> = flow{

        val cacheResult = safeCacheCall(IO){
            noteCacheDataSource.deleteNote(note.id)
        }

        val response = object: CacheResponseHandler<ViewState, Int>(
            response = cacheResult,
            stateEvent = stateEvent
        ){
            override fun handleSuccess(resultObject: Int): DataState<ViewState> {
                return if(resultObject > 0){
                    DataState.data(
                        response = Response(
                            message = DELETE_NOTE_SUCCESS,
                            uiComponentType = UIComponentType.None(),
                            messageType = MessageType.Success()
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                }else{
                    DataState.data(
                        response = Response(
                            message = DELETE_NOTE_FAILURE,
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
           message =  response?.stateMessage?.response?.message,
            note = note
        )

    }

    private suspend fun updateNetwork(message: String?, note : Note){
        if(message.equals(DELETE_NOTE_SUCCESS)){

            // delete from 'notes' node
            safeApiCall(IO){
                noteNetworkDataSource.deleteNote(note.id)
            }

            // insert into 'deletes' node
            safeApiCall(IO){
                noteNetworkDataSource.insertDeletedNote(note)
            }
        }
    }


    companion object{
        const val DELETE_NOTE_SUCCESS = "Successfully deleted the note."
        const val DELETE_NOTE_FAILURE = "Failed deleting the note."
        val DELETE_NOTE_PENDING = "Delete pending..."
        val DELETE_ARE_YOU_SURE = "Are you sure you want to delete this?"
    }


}