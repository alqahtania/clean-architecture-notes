package com.abdull.cleannotes.di

import androidx.room.Room
import com.abdull.cleannotes.business.domain.model.NoteFactory
import com.abdull.cleannotes.framework.datasource.cache.database.NoteDatabase
import com.abdull.cleannotes.framework.datasource.data.NoteDataFactory
import com.abdull.cleannotes.framework.presentation.TestBaseApplication
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Abdullah Alqahtani on 11/22/2020.
 */

@Module
object TestModule {


    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDb(app: TestBaseApplication): NoteDatabase {
        return Room
            .inMemoryDatabaseBuilder(app, NoteDatabase::class.java)
            .fallbackToDestructiveMigration()
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFirestoreSettings() : FirebaseFirestoreSettings{

        return FirebaseFirestoreSettings.Builder()
            .setHost("10.0.2.2:8080")
            .setSslEnabled(false)
            .setPersistenceEnabled(false)
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideFirebaseFirestore(settings : FirebaseFirestoreSettings): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = settings

        return firestore
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNoteDataFactory(
        application: TestBaseApplication,
        notesFactory : NoteFactory
    ) : NoteDataFactory{

        return NoteDataFactory(
            application = application,
            noteFactory =  notesFactory
        )


    }


}





















