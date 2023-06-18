package com.jansellopez.scribby.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jansellopez.scribby.AvatarImage
import com.jansellopez.scribby.CardNote
import com.jansellopez.scribby.R
import com.jansellopez.scribby.data.model.Note
import com.jansellopez.scribby.data.model.User
import com.jansellopez.scribby.data.model.toNote
import com.parse.ParseObject
import com.parse.ParseUser


@Composable
fun SocialUserCard(pair: Pair<ParseUser, MutableList<ParseObject>>) {
    val user = pair.first
    val notes = pair.second.map { it.toNote() }
    if (notes.isEmpty()){
        NoNotesCard(user)
    }else{
        ManyNotesCard(user,notes)
    }
}

@Composable
fun NoNotesCard(user: ParseUser) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)) {
        Surface(shape = CircleShape, modifier= Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = CircleShape
            )
            .padding(4.dp)) {
            AvatarImage(user.username,modifier = Modifier.size(42.dp))
        }
        Text(text = "${user.username} ${stringResource(id = R.string.available_now)}", modifier = Modifier
            .alpha(0.5f)
            .padding(top = 5.dp))
    }
}

@Composable
fun ManyNotesCard(user: ParseUser, notes: List<Note>) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp)
        .heightIn(0.dp, 200.dp), border = BorderStroke(2.dp,MaterialTheme.colorScheme.primary.copy(0.2f))
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)) {
            Row(modifier = Modifier
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                .fillMaxWidth()
                .padding(5.dp), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, modifier= Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = CircleShape
                    )
                    .padding(4.dp)) {
                    AvatarImage(user.username,modifier = Modifier.size(22.dp))
                }
                Text(text = user.username, fontSize = 16.sp)
            }
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                items(notes,{it.objectId!!}){note->
                   CardNote(User(user.username,""),note)
                }
            }
        }

    }
}
