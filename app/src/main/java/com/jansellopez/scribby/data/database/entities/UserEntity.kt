package com.jansellopez.scribby.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jansellopez.scribby.data.model.User

@Entity("user")
data class UserEntity(
    @PrimaryKey(autoGenerate = false) val id:Int = 1,
    val email:String,
    val password:String
)

fun User.toDomain() = UserEntity(1,email,password)
