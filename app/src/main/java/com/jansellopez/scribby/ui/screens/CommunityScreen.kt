package com.jansellopez.scribby.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.jansellopez.scribby.data.model.User
import com.jansellopez.scribby.ui.components.SocialUserCard
import com.jansellopez.scribby.ui.viewmodel.UserViewModel
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import java.util.Date
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jansellopez.scribby.AvatarImage
import com.jansellopez.scribby.CardNote
import com.jansellopez.scribby.R
import com.jansellopez.scribby.core.getFormattedDate
import com.jansellopez.scribby.goToNote
import com.parse.ParseException
import com.valentinilk.shimmer.shimmer
import java.util.Calendar

@Composable
fun CommunityScreen(userViewModel: UserViewModel) {
    val user by userViewModel.user.collectAsState()
    if(user ==null) {
        LoginScreen { u: ParseUser ->
            userViewModel.insertUser(User(u.email,u.sessionToken))
        }
    }else{
        PostScreen(user)
    }
}

@Composable
fun PostScreen(user: User?) {
    var users by rememberSaveable {
        mutableStateOf(emptyList<ParseUser>())
    }
    val query = ParseUser.getQuery()
    query.whereNotEqualTo("username",user?.email?.substringBefore("@"))
    query.findInBackground { us, e ->
        if(e==null){
            users = us
        }else{
            Log.e("B4A-ERROR",e.message.toString())
        }
    }
    if(users.isNotEmpty()) {
        LazyColumn() {
            items(users, key = { it.username }) { user ->
                var notes by rememberSaveable { mutableStateOf(emptyList<ParseObject>()) }
                val queryNote = ParseQuery<ParseObject>("Note")
                queryNote.whereEqualTo("owner", user.username)
                queryNote.whereLessThanOrEqualTo("startDate", Date())
                queryNote.whereGreaterThanOrEqualTo("endDate", Date())
                queryNote.findInBackground { ns, e ->
                    if (e == null) {
                        notes = ns
                    } else {
                        Log.e("B4A-ERROR", e.message.toString())
                    }
                }
                SocialUserCard(Pair(user, notes.toMutableList()))
            }
        }
    }else{
        LoadingPostScreen()
    }
}

@Composable
@Preview
fun LoadingPostScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp).shimmer(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(modifier= Modifier.padding(10.dp),painter = painterResource(id = R.drawable.undraw_the_world_is_mine_re_j5cr), contentDescription = stringResource(
            id = R.string.loading
        ), colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground))
    }
}
