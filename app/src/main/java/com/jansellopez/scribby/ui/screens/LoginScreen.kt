package com.jansellopez.scribby.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jansellopez.scribby.R
import com.jansellopez.scribby.core.isValidEmail
import com.jansellopez.scribby.data.model.User
import com.jansellopez.scribby.ui.components.Indicator
import com.parse.ParseException
import com.parse.ParseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onAuthenticate: (ParseUser) -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Image(painter = painterResource(id = R.drawable.undraw_the_world_is_mine_re_j5cr), modifier=Modifier.padding(20.dp),
            contentDescription = stringResource(id =R.string.community))
        Text(text = stringResource(id = R.string.community), fontSize = 24.sp, fontWeight = FontWeight.Black)
        OutlinedTextField(value =email , onValueChange = {email=it}, maxLines = 1, label = { Text(text =  stringResource(id = R.string.email)) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),modifier = Modifier.padding(top = 10.dp),
        isError = !isValidEmail(email))
        OutlinedTextField(value =password , onValueChange = {password=it}, label = { Text(stringResource(id = R.string.password)) }, maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),modifier = Modifier.padding(top = 10.dp),
            visualTransformation = PasswordVisualTransformation())
        if(isLoading){
            Row(Modifier.padding(top = 10.dp)){}
            Indicator()
        }else{
            Button(onClick = {
                auth(email, password, context,onAuthenticate) {
                    isLoading = !isLoading
                }
            },modifier = Modifier.padding(top = 10.dp)) {
                Text(text = stringResource(id = R.string.get_started))
            }
        }
    }
}


fun auth(
    email: String,
    password: String,
    context: Context,
    onAuthenticate: (ParseUser) -> Unit,
    changeLoading: () -> Unit
) {
    if(isValidEmail(email)) {
        if(password.length>7) {
            changeLoading()
            val user = ParseUser()
            user.username = email.substringBefore("@")
            user.email = email
            user.setPassword(password)
            ParseUser.logInInBackground(user.username,password) { parseUser: ParseUser?, parseException: ParseException? ->
                if (parseUser != null) {
                    onAuthenticate(parseUser)
                    changeLoading()
                    Toast.makeText(context,"${context.resources.getString(R.string.welcome)} ${user.username}", Toast.LENGTH_SHORT).show()
                } else {
                    ParseUser.logOut()
                    if (parseException != null) {
                        Toast.makeText(context, parseException.message, Toast.LENGTH_LONG).show()
                        user.signUpInBackground {
                            if (it == null) {
                                onAuthenticate(user)
                                Toast.makeText(
                                    context,
                                    "${context.resources.getString(R.string.welcome)} ${user.username}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                changeLoading()
                            } else {
                                ParseUser.logOut()
                                changeLoading()
                                Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }
        }else{
            Toast.makeText(context,context.resources.getString(R.string.invalid_password), Toast.LENGTH_SHORT).show()
        }
    }else{
        Toast.makeText(context,context.resources.getString(R.string.invalid_email), Toast.LENGTH_SHORT).show()
    }
}
