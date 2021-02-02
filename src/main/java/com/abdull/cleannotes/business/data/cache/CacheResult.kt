package com.abdull.cleannotes.business.data.cache

/**
 * Created by Abdullah Alqahtani on 10/19/2020.
 */

sealed class CacheResult<out T>{

    data class Success<out T>(val value : T): CacheResult<T>()

    data class GenericError(
        val errorMessage : String? = null
    ) : CacheResult<Nothing>()
}