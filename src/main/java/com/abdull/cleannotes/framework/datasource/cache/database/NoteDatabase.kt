package com.abdull.cleannotes.framework.datasource.cache.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abdull.cleannotes.framework.datasource.cache.model.NoteCacheEntity

/**
 * Created by Abdullah Alqahtani on 11/17/2020.
 */

@Database(entities = [NoteCacheEntity::class], version = 1)
abstract class NoteDatabase : RoomDatabase(){

    abstract fun noteDao() : NoteDao

    companion object{
        const val DATABASE_NAME = "note_db"

    }


}
