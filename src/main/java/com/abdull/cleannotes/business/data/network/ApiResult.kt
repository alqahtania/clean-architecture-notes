package com.abdull.cleannotes.business.data.network

/**
 * Created by Abdullah Alqahtani on 10/19/2020.
 */

sealed class ApiResult<out T>{

    data class Success<out T>(val value: T) : ApiResult<T>()

    data class GenericError(
        val code: Int? = null,
        val errorMessage: String? = null
    ) : ApiResult<Nothing>()

    object NetworkError: ApiResult<Nothing>()
}