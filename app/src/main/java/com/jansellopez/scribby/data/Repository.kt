package com.jansellopez.scribby.data

import com.jansellopez.scribby.data.database.NoteDao
import com.jansellopez.scribby.data.database.entities.NoteEntity
import com.jansellopez.scribby.data.database.entities.UserEntity
import com.jansellopez.scribby.data.database.entities.toDomain
import com.jansellopez.scribby.data.model.Note
import com.jansellopez.scribby.data.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repository @Inject constructor(
    private val noteDao: NoteDao
) {
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()
    suspend fun addNote(note: Note)  = noteDao.insertNote(note.toDomain())
    suspend fun deleteNote(id:Int)  = noteDao.deleteNote(id)
    suspend fun updateNote(note: Note) = noteDao.updateNote(note.toDomain())

    val user: Flow<UserEntity?> = noteDao.getUser()
    suspend fun insertUser(user: User) = noteDao.insertUser(user.toDomain())
}