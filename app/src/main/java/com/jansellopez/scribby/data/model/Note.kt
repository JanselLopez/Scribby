package com.jansellopez.scribby.data.model

import com.jansellopez.scribby.data.database.entities.NoteEntity
import com.parse.ParseObject
import com.parse.ParseUser
import java.util.Calendar

data class Note(
    val id:Int,
    val title:String,
    val description:String,
    val startDate:Calendar,
    val endDate:Calendar,
    val owner:String,
    var objectId:String?=null
)

fun NoteEntity.toDomain() = Note(id, title, description, Calendar.getInstance().apply { timeInMillis = startDate }, Calendar.getInstance().apply { timeInMillis = endDate }, owner,objectId)
fun Note.toParseObject():ParseObject {
    val noteObjectId = objectId
    return ParseObject("Note").apply {
        this.objectId = noteObjectId
        put("title", title)
        put("description", description)
        put("startDate", startDate.time)
        put("endDate", endDate.time)
        put("owner", owner)
    }
}
fun ParseObject.toNote() = Note(
    id= 0,
    title = getString("title")?:"",
    description = getString("description")?:"",
    startDate = (Calendar.getInstance().apply { time = getDate("startDate")?:time }),
    endDate = (Calendar.getInstance().apply { time = getDate("endDate")?:time }),
    owner = getString("owner")?:"",
    objectId=objectId
)