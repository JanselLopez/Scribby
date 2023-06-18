package com.jansellopez.scribby.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jansellopez.scribby.data.Repository
import com.jansellopez.scribby.data.model.Note
import com.jansellopez.scribby.data.model.toDomain
import com.jansellopez.scribby.data.model.toParseObject
import com.jansellopez.scribby.domain.SaveNotesUseCase
import com.parse.ParseObject
import com.parse.ParseQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
        private val repository: Repository,
        private val saveNotesUseCase: SaveNotesUseCase
    ):ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>>
        get() = _notes
    init {
        viewModelScope.launch {
            repository.allNotes.collect {list->
                _notes.value = list.map { it.toDomain() }
            }
        }
    }
    fun insert(note: Note) = viewModelScope.launch {
        val parseNote = note.toParseObject()
        saveNotesUseCase(note)
        parseNote.saveInBackground {e->
            if (e==null){
                note.objectId = parseNote.objectId
            }else{
                Log.e("B4A-ERROR-INSERT",e.message.toString())
            }
        }
    }

    fun update(note: Note) = viewModelScope.launch {
        repository.updateNote(note)
        val query = ParseQuery<ParseObject>("Note")
        val parseNote = note.toParseObject()

        query.limit = 1
        Log.i("note","${note.objectId}")
        query.getInBackground(note.objectId) { note, e ->
            if(e==null){
                val params = listOf("title","description","startDate","endDate","owner")
                params.forEach { param->
                    parseNote.get(param)?.let { note.put(param, it) }
                }
            }else{
                Log.e("B4A-ERROR-UPDATE",e.message.toString())
            }
        }
    }

    fun delete(note:Note) = viewModelScope.launch {
        repository.deleteNote(note.id)
        val query = ParseQuery<ParseObject>("Note")

        query.getInBackground(note.objectId) { note, e ->
            if(e==null){
                note.deleteInBackground {e2->
                    if(e2!=null) Log.e("B4A-ERROR",e2.message.toString())
                }
            }else{
                Log.e("B4A-ERROR-DELETE",e.message.toString())
            }
        }
    }
}