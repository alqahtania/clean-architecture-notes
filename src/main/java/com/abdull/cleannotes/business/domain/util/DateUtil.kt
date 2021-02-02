package com.abdull.cleannotes.business.domain.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Abdullah Alqahtani on 10/19/2020.
 */
@Singleton
class DateUtil @Inject constructor(
    private val dateFormat: SimpleDateFormat
) {

    // date format: "2019-07-23 HH:mm:ss"
    // 2019-07-23

    fun removeTimeFromDateString(sd : String) : String{
        return sd.substring(0, sd.indexOf(" "))
    }

    fun convertFirebaseTimestampToStringDate(timestamp : Timestamp) : String{
        return dateFormat.format(timestamp.toDate())
    }

    fun convertStringDateToFirebaseTimestamp(date : String) : Timestamp{
        return Timestamp(dateFormat.parse(date))

    }

    fun getCurrentTimestamp() : String{
        return dateFormat.format(Date())
    }

}





















