package com.abdull.cleannotes.business.interactors.notelist

import com.abdull.cleannotes.business.data.cache.CacheResponseHandler
import com.abdull.cleannotes.business.data.cache.abstraction.NoteCacheDataSource
import com.abdull.cleannotes.business.data.util.safeCacheCall
import com.abdull.cleannotes.business.domain.model.Note
import com.abdull.cleannotes.business.domain.state.*
import com.abdull.cleannotes.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by Abdullah Alqahtani on 10/21/2020.
 */
class SearchNotes(
    private val noteCacheDataSource : NoteCacheDataSource
) {


    fun searchNotes(
        query : String,
        filterAndOrder : String,
        page : Int,
        stateEvent: StateEvent
    ) : Flow<DataState<NoteListViewState>?>
            = flow{
        var updatedPage = page
        if(page <= 0){
            updatedPage = 1
        }

        val cacheResult = safeCacheCall(IO){
            noteCacheDataSource.searchNotes(
                query = query,
                filterAndOrder = filterAndOrder,
                page = updatedPage
            )
        }

        val response =
            object : CacheResponseHandler<NoteListViewState, List<Note>>(
            response = cacheResult,
            stateEvent = stateEvent
        ){

            override fun handleSuccess(resultObject: List<Note>)
                    : DataState<NoteListViewState> {
                var message = SEARCH_NOTES_SUCCESS
                var uiComponentType : UIComponentType = UIComponentType.None()
                if(resultObject.size == 0){
                    message = SEARCH_NOTES_NO_MATCHING_RESULTS
                    uiComponentType = UIComponentType.Toast()
                }
                return DataState.data(
                    response = Response(
                        message = message,
                        uiComponentType = uiComponentType,
                        messageType = MessageType.Success()
                    ),
                    data = NoteListViewState(
                        noteList = ArrayList(resultObject)
                    ),
                    stateEvent = stateEvent
                )
            }
        }.getResult()

        emit(response)
    }

    companion object{
        val SEARCH_NOTES_SUCCESS = "Successfully retrieved list of notes."
        val SEARCH_NOTES_NO_MATCHING_RESULTS
                = "There are no notes that match that query."
        val SEARCH_NOTES_FAILED = "Failed to retrieve the list of notes."

    }


}