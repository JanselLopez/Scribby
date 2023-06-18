package com.smartestidea.a2fac.core.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jansellopez.scribby.data.database.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {
    @Singleton
    @Provides
    fun getRoomDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context,NotesDatabase::class.java,"notes_db").build()

    @Singleton
    @Provides
    fun getConfigurationDao(db:NotesDatabase) =
        db.getConfigurationDao()
}