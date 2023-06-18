package com.jansellopez.scribby.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jansellopez.scribby.data.model.Note
import java.util.Calendar

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    val title:String,
    val description:String,
    val startDate: Long,
    val endDate: Long,
    val owner:String,
    val objectId:String?=null,
)

fun Note.toDomain() = NoteEntity(id, title, description, startDate.timeInMillis, endDate.timeInMillis, owner,objectId)