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

package com.leo.nopasswordforyou.screen

import android.util.Log
import android.util.Patterns
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import com.leo.nopasswordforyou.components.Spacer
import com.leo.nopasswordforyou.navigation.Screens
import com.leo.nopasswordforyou.viewmodel.LoginVM


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Login(navCtrl: NavHostController) {


    val pager = rememberPagerState {
        3
    }
    val context = LocalContext.current
    val vm = hiltViewModel<LoginVM>()

    vm.HandleBack()
    val pageScroll = rememberScrollState()

    val focus = LocalFocusManager.current


    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        Log.d("TAG", "Login: changing working status in on resume")
        vm.working.value = true
        vm.isLoggedIn.value = !vm.isLoggedIn.value
    }



    LaunchedEffect(key1 = vm.isLoggedIn.value)
    {
        Log.d("TAG", "LogIn: changing working status")
        val verify = vm.checkVerified()
        if (verify != null) {
            if (verify) {
                vm.refreshToken(success = {
                    navCtrl.navigateUp()
                }) {
                    vm.working.value = false
                    vm.handleError("We've encountered an issue while retrieving your account information. Your email is verified, but you might be temporarily unable to access cloud features")
                }
            } else {
                vm.working.value = false
                vm.loginState.value = 3
            }
        } else {
            vm.working.value = false

        }
        /*else {
            navCtrl.popBackStack()
        }*/
    }
    LaunchedEffect(key1 = vm.loginState.value) {
        focus.clearFocus(true)

        pager.animateScrollToPage(
            vm.loginState.value - 1, animationSpec = tween(durationMillis = 500)
        )
        vm.working.value = false
        if (vm.loginState.value == 1) {
            vm.stateBackText.value = "Create account"
        } else vm.stateBackText.value = "Back"
    }

    /*    LaunchedEffect(key1 = vm.passwordReEnterError.value, key2 = vm.passwordEnterError.value) {
            if (vm.passwordReEnterError.value) {
                vm.passwordReEnterLabel.value = "Password not matching"
            } else {
                vm.passwordReEnterLabel.value = ""
            }
        }*/
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.surface)
            .systemBarsPadding()


    ) {//Box


        Column(
            modifier = Modifier
                .padding(top = 30.dp)
                .imePadding()
                .verticalScroll(pageScroll)
        ) { // Column
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 30.dp)

            ) {
                Text(
                    text = "Login",
                    fontSize = typography.headlineMedium.fontSize,
                    color = colorScheme.primary,
                    fontFamily = FontFamily.Monospace
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {


                    AnimatedVisibility(
                        visible = vm.loginState.value == 1, enter = fadeIn(), exit = fadeOut()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "Skip",
                                fontSize = typography.labelLarge.fontSize,
                                modifier = Modifier.clickable(
                                    interactionSource = remember {
                                        MutableInteractionSource()
                                    },
                                    indication = null,
                                ) {
                                    navCtrl.popBackStack()
                                }, color = colorScheme.secondary
                            )
                        }
                    }
                }
            }

            Spacer(height = 5.dp)
            Text(
                text = "To save Passwords on cloud",
                fontSize = typography.labelLarge.fontSize,
                color = colorScheme.secondary,
                modifier = Modifier.padding(horizontal = 30.dp)

            )
            Column(modifier = Modifier.fillMaxSize()) {

                Spacer(height = 100.dp)

                HorizontalPager(state = pager, userScrollEnabled = false) { page ->
                    if (page == 0) {
                        OutlinedTextField(
                            keyboardOptions = KeyboardOptions(
                                autoCorrectEnabled = true,
                                keyboardType = KeyboardType.Email
                            ),
                            value = vm.email.value,
                            onValueChange = {
                                vm.email.value = it
                                vm.emailError.value = ""

                            },
                            singleLine = true,
                            label = { Text(text = "Enter e-mail") },
                            supportingText = {
                                Text(
                                    text = vm.emailError.value,
                                    color = colorScheme.error,
                                    fontSize = typography.labelLarge.fontSize
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp)

                        )
                    }

                    if (page == 1) {

                        OutlinedTextField(
                            keyboardOptions = KeyboardOptions(
                                autoCorrectEnabled = false,
                                keyboardType = KeyboardType.Password
                            ),
                            value = vm.password.value,
                            onValueChange = {
                                vm.password.value = it
                                vm.passwordError.value = ""

                            },
                            label = { Text(text = "password") },
                            singleLine = true,
                            supportingText = {
                                //at least 6 character
                                Text(
                                    text = vm.passwordError.value,
                                    color = colorScheme.error
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp)
                        )
                    }
                    if (page == 2) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row {
                                Text(
                                    text = "Verify Account",
                                    fontSize = typography.titleMedium.fontSize,

                                    modifier = Modifier.padding(10.dp),
                                    color = colorScheme.secondary
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        modifier = Modifier.clickable(
                                            remember { MutableInteractionSource() },
                                            null
                                        ) {
                                            vm.working.value = true
                                            vm.isLoggedIn.value = !vm.isLoggedIn.value
                                        },
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = ""
                                    )
                                }

                            }
                            Spacer(height = 20.dp)

                            Text(
                                fontFamily = FontFamily.SansSerif,
                                text = "We've sent a verification email to ${vm.email.value}. Please click the link inside the mail to confirm your account. If you don't see it, check your spam/junk folder.",
                                color = colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 30.dp, horizontal = 10.dp)
                            )
                        }

                    }
                }

                val ime = WindowInsets.isImeVisible
                val modifier: Modifier = if (!ime) {

                    Modifier
                } else {
                    Modifier.imePadding()
                }


                Spacer(height = 10.dp)

                Column(
                    modifier = modifier,
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 30.dp, end = 30.dp, bottom = 5.dp)
                    ) {
                        AnimatedContent(
                            targetState = vm.stateBackText.value,
                            label = "OnBackClickText"
                        ) {

                            Text(
                                modifier = Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    vm.closeKeyboard(context)
                                    if (vm.loginState.value != 1) {
                                        vm.animDirection.value = false
                                        vm.loginState.value--
                                    } else {
                                        navCtrl.navigate(Screens.SignUp.route) {
                                            popUpTo(Screens.Login.route) { inclusive = true }
                                        }
                                    }

                                },
                                text = it,
                                fontSize = typography.titleMedium.fontSize,
                                color = colorScheme.primary
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End
                        ) {
                            Button(
                                onClick = {
                                    vm.closeKeyboard(context)
                                    vm.working.value = true
                                    if (vm.loginState.value == 1) {
                                        vm.email.value = vm.email.value.trim()
                                        if (Patterns.EMAIL_ADDRESS.matcher(vm.email.value)
                                                .matches()
                                        ) {
                                            vm.animDirection.value = true
                                            vm.loginState.value++
                                        } else {
                                            vm.emailError.value = "e-mail is not valid"
                                            vm.working.value = false
                                        }
                                    } else if (vm.loginState.value == 2) {
                                        if (vm.password.value.length >= 6) {
                                            Log.d("TAG", "Login: Trying login")
                                            vm.loginAccount()
                                        } else {
                                            vm.passwordError.value = "Password too short"
                                            vm.working.value = false
                                        }

                                        //  vm.createAccount()
                                        //create account
                                    } else if (vm.loginState.value == 3) {
                                        //   vm.isLoggedIn.value = !vm.isLoggedIn.value
                                        val verify = vm.checkVerified()
                                        if (verify != null) {
                                            if (verify) {
                                                navCtrl.popBackStack(
                                                    route = Screens.Home.route,
                                                    inclusive = false
                                                )
                                            } else {
                                                // vm.verify()
                                                vm.handleError("Verify your email first. Already done? Wait 30 seconds then try again.")
                                                vm.working.value = false
                                            }
                                        }
                                    }


                                },
                                shape = RoundedCornerShape(12),
                                colors = ButtonDefaults.buttonColors().copy(
                                    containerColor = colorScheme.primaryContainer,
                                    contentColor = colorScheme.onPrimaryContainer
                                )
                            ) {
                                Text(text = "Next", fontSize = typography.labelLarge.fontSize)
                            }


                        }
                    }
                }
                /*}*/
                Spacer(height = 10.dp)

            }
        }


        if (vm.working.value) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = false) {}, color = colorScheme.scrim.copy(0.4f)
            ) {

            }
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        //  val view = LocalView.current
        val snackBar = remember { SnackbarHostState() }
        SnackbarHost(
            hostState = snackBar,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            androidx.compose.material3.Snackbar(it)
        }
        LaunchedEffect(key1 = vm.globalError.value) {
            if (vm.globalError.value) {
                snackBar.showSnackbar(vm.errorText.value, duration = SnackbarDuration.Short)
                vm.clearError()

            }
        }


    }
}


