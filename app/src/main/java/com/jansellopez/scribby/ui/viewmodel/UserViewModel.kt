package com.jansellopez.scribby.ui.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jansellopez.scribby.data.Repository
import com.jansellopez.scribby.data.model.User
import com.jansellopez.scribby.data.model.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: Repository,
) :ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user:StateFlow<User?>
        get() = _user
    init {
        viewModelScope.launch {
            repository.user.collect{
                _user.value = it?.toDomain()
            }
        }
    }
    fun insertUser(user: User){
        viewModelScope.launch {
            repository.insertUser(user)
        }
    }

}