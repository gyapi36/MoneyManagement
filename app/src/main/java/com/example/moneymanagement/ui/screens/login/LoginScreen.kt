package com.example.moneymanagement.ui.screens.login

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymanagement.ui.theme.LoginBackground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    context: Context = LocalContext.current,
) {
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("gyuszi3318@gmail.com") }
    var password by rememberSaveable { mutableStateOf("123456") }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .background(LoginBackground)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxSize()
                .padding(top = 60.dp),
        ) {
            Image(
                painter = painterResource(com.example.moneymanagement.R.drawable.theme),
                contentDescription = "Main logo",
                modifier = Modifier
                    .size(300.dp)
                    .padding(top = 1.dp)
            )
            Text(
                text = "Money SMART",
                fontSize = 30.sp,
                color = Color.Black,
                fontFamily = loginViewModel.myFont
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 300.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                label = {
                    Text(text = "E-mail")
                },
                value = email,
                onValueChange = {
                    email = it
                },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Email, null)
                }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.8f),
                label = {
                    Text(text = "Password")
                },
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(Icons.Default.Password, null)
                },
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        if (showPassword) {
                            Icon(Icons.Default.Visibility, null)
                        } else {
                            Icon(Icons.Default.VisibilityOff, null)
                        }
                    }
                }
            )
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        val result = loginViewModel.loginUser(email, password)
                        if (result?.user != null) {
                            withContext(Dispatchers.Main) {
                                onLoginSuccess()
                            }
                        }
                    }
                }) {
                    Text(
                        text = "Login",
                        color = Color.Black
                    )
                }
                OutlinedButton(onClick = {
                    // do registration..
                    coroutineScope.launch {
                        loginViewModel.registerUser(email, password)
                    }
                }) {
                    Text(
                        text = "Register",
                        color = Color.Black
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (loginViewModel.loginUiState) {
                is LoginUiState.Loading -> CircularProgressIndicator()
                is LoginUiState.RegisterSuccess -> Text(text = "Registration OK")
                is LoginUiState.Error -> Text(
                    text = "Error: ${
                        (loginViewModel.loginUiState as LoginUiState.Error).error
                    }"
                )
                is LoginUiState.LoginSuccess -> {}
                LoginUiState.Init -> {}
            }
        }
    }
}