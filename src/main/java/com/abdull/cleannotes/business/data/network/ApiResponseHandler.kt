package com.abdull.cleannotes.business.data.network

import com.abdull.cleannotes.business.data.network.NetworkErrors.NETWORK_DATA_NULL
import com.abdull.cleannotes.business.data.network.NetworkErrors.NETWORK_ERROR
import com.abdull.cleannotes.business.domain.state.*

/**
 * Created by Abdullah Alqahtani on 10/19/2020.
 */

abstract class ApiResponseHandler<ViewState, Data>(
    private val response : ApiResult<Data?>,
    private val stateEvent : StateEvent?
){
    suspend fun getResult() : DataState<ViewState>{
        return when(response){
            is ApiResult.GenericError -> {
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
            is ApiResult.NetworkError ->{

                DataState.error(
                    response = Response(
                        message = "${stateEvent?.errorInfo()}\n\n" +
                                "Reason: $NETWORK_ERROR",
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    ),
                    stateEvent = stateEvent
                )

            }
            is ApiResult.Success -> {
                if(response.value == null){
                    DataState.error(
                        response = Response(
                            message = "${stateEvent?.errorInfo()}\n\n" +
                                    "Reason: $NETWORK_DATA_NULL",
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        ),
                        stateEvent = stateEvent
                    )
                }else{
                    handleSuccess(response.value)
                }
            }
        }
    }
    abstract suspend fun handleSuccess(resultObject : Data) : DataState<ViewState>
}