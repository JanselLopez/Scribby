package com.jansellopez.scribby.ui.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.IntentSender.OnFinished
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jansellopez.scribby.data.model.Note
import java.util.Calendar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jansellopez.scribby.R
import com.jansellopez.scribby.core.getFormattedDate
import com.jansellopez.scribby.ui.theme.ScribbyTheme
import com.jansellopez.scribby.ui.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
var globalNote:Note?=null
@AndroidEntryPoint
class DetailActivity : ComponentActivity() {
    private val noteViewModel:NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getIntExtra("id",0)
        val title = intent.getStringExtra("title")?:""
        val description = intent.getStringExtra("description")?:""
        val startDate = Calendar.getInstance().apply{ timeInMillis = intent.getLongExtra("startDate", Calendar.getInstance().timeInMillis) }
        val endDate = Calendar.getInstance().apply{ timeInMillis= intent.getLongExtra("endDate", Calendar.getInstance().timeInMillis) }
        val owner = intent.getStringExtra("owner")?:""
        val objectId = intent.getStringExtra("objectId")
        val note = Note(id, title, description, startDate, endDate, owner,objectId)
        setContent {
            ScribbyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DetailScreen(note, { onBackPressedDispatcher.onBackPressed() },noteViewModel)
                }
            }
        }
    }

    override fun onBackPressed() {
        globalNote?.apply {
            if (id != 0)
                noteViewModel.update(this)
            else
                noteViewModel.insert(this)
        }
        onBackPressedDispatcher.onBackPressed()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(note:Note = Note(1,"${1}th note","lorem ipsumlorem ipsumlorem ipsumlorem ipsumlorem ipsum", Calendar.getInstance(),
    Calendar.getInstance(),"21jansel"), onBackPressed: () -> Unit,noteViewModel: NoteViewModel){
    var currentNote by remember {
        mutableStateOf(note)
    }
    LaunchedEffect(currentNote){
        globalNote = currentNote
    }
    val context = LocalContext.current
    Scaffold(
        topBar = {AppBar(currentNote, onBackPressed = onBackPressed,noteViewModel )}
    ) {innerPadding->
        currentNote.apply {
            Column(modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()) {
                LazyColumn(modifier = Modifier.weight(11f)){
                    item{
                        TextField(value = title, onValueChange = {currentNote = currentNote.copy(title = it)},
                            textStyle = TextStyle.Default.copy(fontSize = 24.sp, fontWeight = FontWeight.Black),
                        modifier = Modifier.fillMaxWidth(), placeholder = {Text(text = stringResource(
                                id = R.string.title
                            ),fontSize = 24.sp, fontWeight = FontWeight.Black)}, colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                containerColor = Color.Transparent
                            ))
                    }
                    item {
                        TextField(value = description, onValueChange = {currentNote = currentNote.copy(description = it)},
                            modifier = Modifier.fillMaxWidth(), placeholder = { Text(text = stringResource(
                                id = R.string.write_your_note_here
                            ))}, colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                containerColor = Color.Transparent
                            ))
                    }
                }
                Row(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = {
                        selectDate(context) { selectedDateTime: Calendar ->
                            if(selectedDateTime <= currentNote.endDate)
                                currentNote = currentNote.copy(startDate = selectedDateTime)
                            else
                                Toast.makeText(context,context.resources.getString(R.string.start_date_cannot_be_greater_than_end_date),Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text(text = getFormattedDate(startDate))
                    }
                    Text(text = "-")
                    TextButton(onClick = {
                        selectDate(context) { selectedDateTime: Calendar ->
                            if(selectedDateTime>=currentNote.startDate)
                                currentNote = currentNote.copy(endDate = selectedDateTime)
                            else
                                Toast.makeText(context,context.resources.getString(R.string.end_date_cannot_be_less_than_start_date),Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text(text = getFormattedDate(endDate))
                    }
                }

            }
        }
    }
}

fun selectDate(context: Context, onFinish: (Calendar) -> Unit) {
    val now = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,{_,year,month,dayOfMonth->
            val timePicker = TimePickerDialog(
                context,
                {_,hourOfDay, minute->
                    val selectedDateTime = Calendar.getInstance().apply {
                        set(year,month,dayOfMonth,hourOfDay, minute)
                    }
                    onFinish(selectedDateTime)
                },now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE), false)
            timePicker.show()
        },now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.show()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(note: Note,onBackPressed:()->Unit,noteViewModel: NoteViewModel) {
    TopAppBar(
        title = {},
        navigationIcon = { IconButton(onClick = {
            if(note.id == 0)
                noteViewModel.insert(note)
            else
                noteViewModel.update(note)
            onBackPressed()
        }) {
            Icon(imageVector = Icons.Outlined.ArrowBack,
                contentDescription = stringResource(id = R.string.goBack))
        }
        },
        actions = {
            IconButton(onClick = {
                noteViewModel.delete(note)
                onBackPressed()
            }) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = stringResource(id =  R.string.delete))
            }
        }
    )
}
