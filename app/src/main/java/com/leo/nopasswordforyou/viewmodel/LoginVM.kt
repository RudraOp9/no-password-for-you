/*
 *  No password for you
 *  Copyright (c) 2024 . All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,either version 3 of the License,or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not,see <http://www.gnu.org/licenses/>.
 */

package com.leo.nopasswordforyou.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginVM : ViewModel() {


    private val auth = FirebaseAuth.getInstance()

    var isLoggedIn: MutableState<Boolean> = mutableStateOf(false)

    // Sign up
    var email: MutableState<String> = mutableStateOf("")
    var emailError: MutableState<String> = mutableStateOf("")

    var password: MutableState<String> = mutableStateOf("")
    var passwordError: MutableState<String> = mutableStateOf("")

    val working: MutableState<Boolean> = mutableStateOf(false)
    val globalError: MutableState<Boolean> = mutableStateOf(false)
    val errorText: MutableState<String> = mutableStateOf("")

    var animDirection: MutableState<Boolean> = mutableStateOf(true)


    var stateBackText: MutableState<String> = mutableStateOf("")


    var loginState: MutableState<Int> = mutableIntStateOf(1)

    init {
        isLoggedIn.value = auth.currentUser != null
    }

    fun checkVerified(): Boolean? {
        if (auth.currentUser != null) {
            auth.currentUser?.reload()
        }
        return if (auth.currentUser != null) {
            auth.currentUser!!.isEmailVerified
        } else null
    }

    fun loginAccount() {
        auth.signInWithEmailAndPassword(email.value, password.value).addOnSuccessListener {
            isLoggedIn.value = !isLoggedIn.value
            if (auth.currentUser?.isEmailVerified == false) {
                verify()
            }
            working.value = false
        }.addOnFailureListener {
            isLoggedIn.value = !isLoggedIn.value
            //    passwordError.value =
            handleError(it.localizedMessage ?: "something went wrong")
            working.value = false
        }
    }

    private fun verify() {
        auth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
            //   loginState.value = 3
            auth.currentUser?.reload()
            isLoggedIn.value = !isLoggedIn.value
            working.value = false
        }?.addOnFailureListener {
            handleError(it.localizedMessage ?: "something went wrong")
            working.value = false
        }
    }

    fun handleError(message: String) {
        errorText.value = message
        globalError.value = true
        Log.d("TAG", "handleError: handle error $message")
    }

    fun clearError() {
        globalError.value = false
        errorText.value = ""
    }

    @Composable
    fun HandleBack() {
        BackHandler(loginState.value != 1) {
            loginState.value--
        }
    }

    fun closeKeyboard(context: Context) {
        val inputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusedView = (context as? Activity)?.currentFocus
        if (currentFocusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusedView.windowToken, 0)
        }
    }

    fun refreshToken(success: () -> Unit, error: () -> Unit) {
        auth.currentUser?.getIdToken(true)?.addOnCompleteListener {

            if (it.isSuccessful) {
                success.invoke()
            } else {
                error.invoke()
            }
        }
    }


}