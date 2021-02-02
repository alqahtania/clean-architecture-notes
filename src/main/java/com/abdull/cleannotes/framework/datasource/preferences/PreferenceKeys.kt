package com.abdull.cleannotes.framework.datasource.preferences

/**
 * Created by Abdullah Alqahtani on 11/26/2020.
 */


class PreferenceKeys {

    companion object{

        // Shared Preference Files:
        const val NOTE_PREFERENCES: String = "com.codingwithmitch.cleannotes.notes"

        // Shared Preference Keys
        val NOTE_FILTER: String = "${NOTE_PREFERENCES}.NOTE_FILTER"
        val NOTE_ORDER: String = "${NOTE_PREFERENCES}.NOTE_ORDER"

    }
}