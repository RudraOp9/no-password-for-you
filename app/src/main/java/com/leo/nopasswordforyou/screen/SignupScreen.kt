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
import android.view.WindowManager
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.leo.nopasswordforyou.components.Spacer
import com.leo.nopasswordforyou.navigation.Screens
import com.leo.nopasswordforyou.viewmodel.SignUpVM


@OptIn(ExperimentalLayoutApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SignUp(navCtrl: NavHostController) {

    val vm: SignUpVM = viewModel()
    val ime = WindowInsets.isImeVisible
    val focus = LocalFocusManager.current

    vm.HandleBack()
    val context = LocalContext.current


    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        Log.d("TAG", "SignUp: changing working status in on resume")
        vm.closeKeyboard(context)
        vm.working.value = true
        vm.isLoggedIn.value = !vm.isLoggedIn.value
    }

    LaunchedEffect(key1 = vm.isLoggedIn.value) {
        Log.d("TAG", "SignUp: changing working status")
        val verify = vm.checkVerified()
        if (verify != null) {
            if (verify) {
                vm.refreshToken(success = {
                    navCtrl.popBackStack()

                }) {
                    vm.working.value = false
                    vm.handleError("We've encountered an issue while retrieving your account information. Your email is verified, but you might be temporarily unable to access cloud features")
                }
            } else {
                // vm.verify()
                vm.signUpState.value = 3
                vm.working.value = false

            }
        } else {
            vm.working.value = false

        }
    }


    val pagerState = rememberPagerState(initialPage = 0) {
        3
    }
    val pageScroll = rememberScrollState()
    LaunchedEffect(key1 = vm.signUpState.value) {
        pagerState.animateScrollToPage(
            vm.signUpState.value - 1,
            animationSpec = tween(durationMillis = 500)
        )
        focus.clearFocus(true)
        vm.working.value = false
        if (vm.signUpState.value == 1) {
            vm.stateBackText.value = "Login instead"
        } else vm.stateBackText.value = "Back"
        if (ime) {
            pageScroll.animateScrollTo(pageScroll.maxValue, tween(durationMillis = 250))
        }
    }


    LaunchedEffect(key1 = vm.passwordReEnterError.value, key2 = vm.passwordEnterError.value) {
        if (vm.passwordReEnterError.value) {
            vm.passwordReEnterLabel.value = "Password not matching"
        } else {
            vm.passwordReEnterLabel.value = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorScheme.surface)
            .systemBarsPadding()

    ) {


        Column(
            modifier = Modifier
                .background(color = colorScheme.surface)
                .padding(top = 30.dp)
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(pageScroll)
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 30.dp)
            ) {
                Text(
                    text = "Create Account",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = typography.headlineMedium.fontSize,
                    color = colorScheme.primary,
                )

                AnimatedVisibility(
                    visible = vm.signUpState.value == 1,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "Skip",
                            fontFamily = FontFamily.SansSerif,
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

            Spacer(height = 5.dp)
            Text(
                text = "To save Passwords on cloud",
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(horizontal = 30.dp),
                fontSize = typography.labelLarge.fontSize,
                color = colorScheme.secondary
            )
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(height = 100.dp)

                HorizontalPager(
                    userScrollEnabled = false,
                    state = pagerState
                ) {

                    if (it == 0) {
                        OutlinedTextField(
                            keyboardOptions = KeyboardOptions(
                                autoCorrectEnabled = true,
                                keyboardType = KeyboardType.Email
                            ),
                            value = vm.email.value,
                            onValueChange = { newVal ->
                                vm.email.value = newVal
                                vm.emailError.value = ""
                            },
                            label = {
                                Text(
                                    fontFamily = FontFamily.SansSerif,
                                    text = "Enter e-mail"
                                )
                            },
                            singleLine = true,
                            supportingText = {
                                Text(
                                    //e-mail is not valid
                                    text = vm.emailError.value,
                                    fontFamily = FontFamily.SansSerif,
                                    color = colorScheme.error,
                                    fontSize = typography.labelLarge.fontSize
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp)
                        )
                    }
                    if (it == 1) {
                        Column {


                            OutlinedTextField(
                                keyboardOptions = KeyboardOptions(
                                    autoCorrectEnabled = false,
                                    keyboardType = KeyboardType.Password
                                ),
                                value = vm.password.value,
                                onValueChange = { newVal ->
                                    vm.password.value = newVal
                                    vm.passwordEnterError.value = false
                                },
                                label = {
                                    Text(
                                        fontFamily = FontFamily.SansSerif,
                                        text = "Create password"
                                    )
                                },
                                singleLine = true,
                                supportingText = {
                                    //at least 6 character
                                    Text(
                                        text = "min 6 chars: letters, numbers & symbols",
                                        fontFamily = FontFamily.SansSerif,
                                        color = if (vm.passwordEnterError.value) colorScheme.error else Color.Unspecified
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 30.dp)
                            )


                            Spacer(height = 10.dp)
                            OutlinedTextField(
                                keyboardOptions = KeyboardOptions(
                                    autoCorrectEnabled = false,
                                    keyboardType = KeyboardType.Password
                                ),

                                value = vm.passwordReenter.value,
                                onValueChange = { newVal ->
                                    vm.passwordReenter.value = newVal
                                    vm.passwordReEnterError.value = false
                                },
                                label = {
                                    Text(
                                        fontFamily = FontFamily.SansSerif,
                                        text = "Re-enter password"
                                    )
                                },
                                singleLine = true,
                                supportingText = {
                                    //at least 6 character
                                    Text(
                                        fontFamily = FontFamily.SansSerif,
                                        text = vm.passwordReEnterLabel.value,
                                        color = if (vm.passwordReEnterError.value) colorScheme.error else Color.Unspecified
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 30.dp)
                            )
                        }
                    }
                    if (it == 2) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Verify Account",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = typography.titleLarge.fontSize,
                                    modifier = Modifier.padding(10.dp),
                                    color = colorScheme.primary
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



                Spacer(height = 10.dp)

                val modifier: Modifier = if (!ime) {

                    Modifier.fillMaxSize()

                } else {
                    LaunchedEffect(key1 = Unit) {
                        pageScroll.animateScrollTo(pageScroll.maxValue, tween())

                    }
                    Modifier.imePadding()

                }

                Column(
                    modifier = modifier
                        .padding(horizontal = 30.dp), verticalArrangement = Arrangement.Bottom
                ) {


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AnimatedContent(
                            targetState = vm.stateBackText.value,
                            label = "OnBackClickText"
                        ) {


                            Text(
                                fontFamily = FontFamily.SansSerif,
                                modifier = Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    vm.closeKeyboard(context)
                                    if (vm.signUpState.value != 1) {
                                        vm.animDirection.value = false
                                        vm.signUpState.value--
                                    } else {
                                        navCtrl.navigate(Screens.Login.route) {
                                            popUpTo(Screens.SignUp.route) { inclusive = true }
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
                                    if (vm.signUpState.value == 1) {
                                        vm.email.value = vm.email.value.trim()
                                        if (Patterns.EMAIL_ADDRESS.matcher(vm.email.value)
                                                .matches()
                                        ) {
                                            vm.animDirection.value = true
                                            vm.signUpState.value++

                                        } else {
                                            vm.emailError.value = "e-mail is not valid"
                                            vm.working.value = false
                                        }
                                    } else if (vm.signUpState.value == 2) {
                                        val hasLetter = vm.password.value.any { it.isLetter() }
                                        val hasNumber = vm.password.value.any { it.isDigit() }
                                        //    val hasSymbol = vm.email.value.any { !it.isLetterOrDigit() }
                                        if (hasLetter && hasNumber) {
                                            if (vm.password.value == vm.passwordReenter.value) {
                                                vm.createAccount()
                                            } else {
                                                vm.passwordReEnterError.value = true
                                                vm.working.value = false
                                            }

                                        } else {
                                            vm.passwordEnterError.value = true
                                            vm.working.value = false
                                        }
                                        //create account

                                    } else if (vm.signUpState.value == 3) {
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

                                Text(
                                    fontFamily = FontFamily.SansSerif,
                                    text = "Next", fontSize = typography.labelLarge.fontSize
                                )
                            }


                        }
                    }
                }
                Spacer(height = 10.dp)

            }


        }
        if (vm.working.value) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = false) {},
                color = colorScheme.surface.copy(alpha = 0.5f)
            ) {

            }
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        //  val view = LocalView.current
        val snackBar = remember {
            SnackbarHostState()
        }
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
