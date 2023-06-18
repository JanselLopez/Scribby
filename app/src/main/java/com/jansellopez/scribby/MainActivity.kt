package com.jansellopez.scribby

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jansellopez.scribby.core.getFormattedDate
import com.jansellopez.scribby.data.model.Note
import com.jansellopez.scribby.data.model.User
import com.jansellopez.scribby.ui.screens.CommunityScreen
import com.jansellopez.scribby.ui.theme.ScribbyTheme
import com.jansellopez.scribby.ui.view.DetailActivity
import com.jansellopez.scribby.ui.viewmodel.NoteViewModel
import com.jansellopez.scribby.ui.viewmodel.UserViewModel
import com.valentinilk.shimmer.shimmer
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val userViewModel:UserViewModel by viewModels()
    private val noteViewModel:NoteViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScribbyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScribbyApp(noteViewModel = noteViewModel,userViewModel= userViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScribbyApp(noteViewModel: NoteViewModel, userViewModel: UserViewModel) {
    val routes = listOf(Pair(stringResource(id = R.string.home), R.drawable.note_text_svgrepo_com),Pair(stringResource(id = R.string.community), R.drawable.people_svgrepo_com))
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentStack = currentBackStack?.destination
    val currentScreen = routes.find { screen -> screen.first == currentStack?.route }?:routes[0]
    val user by userViewModel.user.collectAsState()

    Scaffold (
    topBar = {TopBar(user,routes, currentScreen){
        route -> navController.navigate(route){launchSingleTop=true}
    } },
    floatingActionButton ={FabNewNote(user) }
    ){innerPadding->
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding), horizontalAlignment = Alignment.CenterHorizontally) {
            ScribbyHost(routes = routes, noteViewModel = noteViewModel,userViewModel=userViewModel,navController = navController)
        }
    }
}

@Composable
fun ScribbyHost(
    routes: List<Pair<String, Int>>,
    noteViewModel: NoteViewModel,
    navController: NavHostController,
    userViewModel: UserViewModel
){
    NavHost(navController = navController, startDestination = routes[0].first){
        composable(routes[0].first){
            HomeScreen(userViewModel,noteViewModel)
        }
        composable(routes[1].first){
            CommunityScreen(userViewModel)
        }
    }

}

@Composable
fun HomeScreen(userViewModel: UserViewModel, noteViewModel: NoteViewModel){
    val notes by noteViewModel.notes.collectAsState()
    if(notes.isEmpty()) 
        WelcomeScreen()
    else
        NotesScreen(userViewModel,notes)
   
}

@Composable
@Preview(showBackground = true)
fun WelcomeScreen() {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(20.dp)
        .alpha(0.5f).shimmer(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Image(modifier= Modifier.padding(10.dp),painter = painterResource(id = R.drawable.undraw_no_data_re_kwbl), contentDescription = stringResource(
            id = R.string.welcome
        ), colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground))
    }
}

@Composable
fun NotesScreen(userViewModel: UserViewModel, notes: List<Note>) {
    val user by userViewModel.user.collectAsState()
    LazyColumn(modifier = Modifier
        .animateContentSize()){
        items(notes, key = {"${it.id}-${it.title}-${user?.email?:"none"}-local"}){note->
            CardNote(user=user,note = note)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardNote(user:User?,note: Note =Note(1,"${1}th note","lorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsum", Calendar.getInstance(),Calendar.getInstance(),"21jansel")){
    val context = LocalContext.current
    ElevatedCard(onClick = {
        goToNote(note,context)
    }, modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(IntrinsicSize.Min)) {
            Column(modifier = Modifier.weight(5f)) {

                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    if(user!=null && user.email.substringBefore("@") != note.owner) {
                        Surface(
                            shape = CircleShape, modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    shape = CircleShape
                                )
                                .padding(4.dp)
                        ) {
                            AvatarImage(
                                username= note.owner,modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                    Text(
                        text = note.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(text = note.description, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.alpha(0.5f))
                Text(text = "${getFormattedDate(note.startDate)} - ${getFormattedDate(note.endDate)}",modifier = Modifier.alpha(0.5f), fontSize = 12.sp)
            }
            val currentDate = Calendar.getInstance()
            val percent = ((currentDate.timeInMillis - note.startDate.timeInMillis).toFloat()/
                    (note.endDate.timeInMillis-note.startDate.timeInMillis).toFloat())
            Box(modifier = Modifier
                .weight(1f)
                .fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(progress = if(percent.isFinite() && percent<1f) percent else 1f, strokeWidth = (-5).dp)
                Text(text = if(percent.isFinite() && percent<1f) "${(percent*100).toInt()}%" else "100%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

fun goToNote(note: Note, context: Context) {
    note.apply {
        val intent = Intent(context,DetailActivity::class.java)
            .putExtra("id",id)
            .putExtra("title",title)
            .putExtra("description",description)
            .putExtra("startDate",startDate.timeInMillis)
            .putExtra("endDate",endDate.timeInMillis)
            .putExtra("owner",owner)
            .putExtra("objectId",objectId)
        context.startActivity(intent)
    }
}

@Composable
fun FabNewNote(user: User?) {
    val context = LocalContext.current
    FloatingActionButton(onClick = {
        val newNote = Note(0,"","", Calendar.getInstance(),Calendar.getInstance(),user?.email?.substringBefore("@")?:"")
        goToNote(newNote,context)
    }, modifier = Modifier.animateContentSize()) {
        Row {
            Icon(imageVector = Icons.Filled.Add, contentDescription = stringResource(id = R.string.add_new_note))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(modifier: Modifier=Modifier) {
    var value by rememberSaveable {
        mutableStateOf("")
    }
    TextField(value = value, onValueChange = {value =it},
        leadingIcon = { Icon(imageVector = Icons.Outlined.Search,
        contentDescription = stringResource(id = R.string.search))},
        modifier=modifier.fillMaxWidth(), shape = CircleShape,
        placeholder = { Text(text = stringResource(id = R.string.search_notes)) },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ))
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AvatarImage(username:String,modifier:Modifier = Modifier){
    GlideImage(
        model = "https://api.dicebear.com/6.x/bottts-neutral/jpg?seed=$username",
        contentDescription = "profile photo",
        modifier= modifier.background(MaterialTheme.colorScheme.onBackground),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    user: User?,
    routes: List<Pair<String, Int>>,
    currentScreen: Pair<String, Int>,
    onTabPress: (route: String) -> Unit,
) {
    var username by rememberSaveable {
        mutableStateOf("Scribble")
    }
    LaunchedEffect(key1 = user ){
        if(user!=null) username = user.email.substringBefore("@")
    }
    TopAppBar(
        title = { Row {
            Surface(shape = CircleShape, modifier= Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = CircleShape
                )
                .padding(4.dp)) {
                AvatarImage(username,modifier = Modifier.size(35.dp))
            }
        }},
        actions = {
            Row{
                routes.forEach { route->
                    IconButton(onClick = { onTabPress(route.first) }) {
                        Icon(painter = painterResource(id = route.second),
                            contentDescription = route.first,
                            tint = if(route == currentScreen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        },
    )
}

