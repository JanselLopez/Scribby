package com.jansellopez.scribby.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jansellopez.scribby.data.database.entities.NoteEntity
import com.jansellopez.scribby.data.database.entities.UserEntity

@Database(entities = [NoteEntity::class,UserEntity::class], version = 1)
abstract class NotesDatabase:RoomDatabase() {
    abstract fun getConfigurationDao():NoteDao

}