package com.abdull.cleannotes.business.data.cache

import com.abdull.cleannotes.business.data.cache.CacheErrors.CACHE_DATA_NULL
import com.abdull.cleannotes.business.domain.state.*

/**
 * Created by Abdullah Alqahtani on 10/19/2020.
 */

abstract class CacheResponseHandler<ViewState, Data>(
    private val response : CacheResult<Data?>,
    private val stateEvent : StateEvent?
){
    suspend fun getResult() : DataState<ViewState>?{
        return when(response){
            is CacheResult.GenericError -> {
                DataState.error(
                    response = Response(
                        message = "${stateEvent?.errorInfo()}\n\n" +
                                "Reason: ${response.errorMessage}",
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    ),
                    stateEvent = stateEvent
                )
            }
            is CacheResult.Success -> {
                if(response.value == null){
                    DataState.error(
                        response = Response(
                            message = "${stateEvent?.errorInfo()}\n\n" +
                                    "Reason: ${CACHE_DATA_NULL}",
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        ),
                        stateEvent = stateEvent
                    )
                }else{
                    handleSuccess(resultObject = response.value)
                }
            }
        }
    }

    abstract fun handleSuccess(resultObject : Data) : DataState<ViewState>?
}