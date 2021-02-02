package com.abdull.cleannotes.business.data

import com.abdull.cleannotes.business.domain.model.Note
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by Abdullah Alqahtani on 10/29/2020.
 */

//ClassLoader allows us to access resources and files in android
//because in testing we don't have access to the context the classLoader
//helps us access those resources without the context
class NoteDataFactory (
    private val testClassLoader: ClassLoader
){
    private val jsonFile = "note_list.json"
    fun produceListOfNotes(): List<Note>{
        val notes : List<Note> = Gson()
            .fromJson(
                getNotesFromFile(jsonFile),
                object : TypeToken<List<Note>>(){}.type
            )
        return notes
    }

    fun produceHashMapOfNotes(noteList : List<Note>): HashMap<String, Note>{
        val map = HashMap<String, Note>()
        for(note in noteList){
            map.put(note.id, note)
        }
        return map
    }

    fun produceEmptyListOfNotes(): List<Note>{
        return ArrayList()
    }

    fun getNotesFromFile(fileName: String) : String{
        return testClassLoader.getResource(fileName).readText()
    }


}