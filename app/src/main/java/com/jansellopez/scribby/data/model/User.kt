package com.jansellopez.scribby.data.model

import com.jansellopez.scribby.data.database.entities.UserEntity


data class User(
    val email:String,
    val password:String,
)

fun UserEntity.toDomain() = User(email,password)