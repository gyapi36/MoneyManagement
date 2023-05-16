package com.example.moneymanagement.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import com.example.moneymanagement.R
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

sealed interface LoginUiState {
    object Init : LoginUiState
    object Loading : LoginUiState
    object LoginSuccess : LoginUiState
    object RegisterSuccess : LoginUiState
    data class Error(val error: String?) : LoginUiState
}

class LoginViewModel : ViewModel() {

    var loginUiState: LoginUiState by mutableStateOf(LoginUiState.Init)

    private var auth: FirebaseAuth = Firebase.auth

    val myFont = FontFamily(Font(R.font.king_font))

    suspend fun registerUser(email: String, password: String) {
        loginUiState = LoginUiState.Loading
        delay(2000)

        try {
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                loginUiState = LoginUiState.RegisterSuccess
            }.addOnFailureListener {
                loginUiState = LoginUiState.Error(it.message)
            }
        } catch (e: java.lang.Exception) {
            loginUiState = LoginUiState.Error(e.message)
        }
    }

    suspend fun loginUser(email: String, password: String) : AuthResult? {
        loginUiState = LoginUiState.Loading

        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()

            loginUiState = LoginUiState.LoginSuccess

            result
        } catch (e: java.lang.Exception) {
            loginUiState = LoginUiState.Error(e.message)

            null
        }
    }
}