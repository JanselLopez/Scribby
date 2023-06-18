package com.jansellopez.scribby.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jansellopez.scribby.data.database.entities.NoteEntity
import com.jansellopez.scribby.data.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note:NoteEntity)
    @Update
    suspend fun updateNote(note: NoteEntity)
    @Query("SELECT * FROM notes ORDER BY endDate")
    fun getAllNotes(): Flow<List<NoteEntity>>
    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id:Int)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userEntity: UserEntity)
    @Query("SELECT * FROM user LIMIT 1")
    fun getUser():Flow<UserEntity?>
}