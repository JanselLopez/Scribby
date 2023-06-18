package com.jansellopez.scribby.domain

import com.jansellopez.scribby.data.Repository
import com.jansellopez.scribby.data.model.Note
import javax.inject.Inject

class SaveNotesUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(note: Note) =  repository.addNote(note)

}