package com.abdull.cleannotes.framework.datasource.data

import android.app.Application
import android.content.res.AssetManager
import com.abdull.cleannotes.business.domain.model.Note
import com.abdull.cleannotes.business.domain.model.NoteFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Abdullah Alqahtani on 11/23/2020.
 */

@Singleton
class NoteDataFactory
@Inject
constructor(
    private val application : Application,
    private val noteFactory : NoteFactory
){

    fun produceListOfNotes() : List<Note>{
        val notes : List<Note> = Gson()
            .fromJson(
                readJSONFromAsset("note_list.json"),
                object : TypeToken<List<Note>>(){}.type
            )
        return notes

    }

    fun produceEmptyListOfNotes() : List<Note>{
        return ArrayList()
    }
    private fun readJSONFromAsset(fileName : String) : String?{

        var json : String? = null

        json = try {

            val inputStream : InputStream = (application.assets as AssetManager).open(fileName)
            inputStream.bufferedReader().use {
                it.readText()
            }

        }catch (e: IOException){
            e.printStackTrace()
            return null
        }
        return json
    }

    fun createSingleNote(
        id: String? = null,
        title: String,
        body : String? = null
    ) = noteFactory.createSingleNote(id, title, body)

    fun createNoteList(numNotes : Int) = noteFactory.createNoteList(numNotes)




}