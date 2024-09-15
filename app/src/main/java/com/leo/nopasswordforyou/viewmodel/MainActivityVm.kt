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

import android.app.Activity.MODE_PRIVATE
import android.app.KeyguardManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.leo.nopasswordforyou.R
import com.leo.nopasswordforyou.database.passes.PassesEntity
import com.leo.nopasswordforyou.database.passlist.PassListEntity
import com.leo.nopasswordforyou.secuirity.Security
import com.leo.nopasswordforyou.util.NewPass
import com.leo.nopasswordforyou.util.sorting.SortOrder
import com.leo.nopasswordforyou.util.sorting.Sorting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max
import kotlin.time.Duration

@HiltViewModel
class MainActivityVm @Inject constructor(
) : ViewModel() {

    var showDeleteDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var showCloudDel: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val userInputChannel = Channel<Boolean>()


    val sortDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)


    var passWord: String = ""
    var passwordCopyIcon: MutableStateFlow<Int> = MutableStateFlow(R.drawable.icon_copy_24)

    val working: MutableState<Boolean> = mutableStateOf(false)

    var sortBy: Sorting = Sorting.Title
    var sortOrder: SortOrder = SortOrder.Ascending


    var decodedData: String? = null
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val newPass: NewPass = NewPass()


    var startSelection: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var selectAll: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var selected: MutableStateFlow<Set<Int>> = MutableStateFlow(emptySet())

    var themeIcon: MutableStateFlow<Int> = MutableStateFlow(R.drawable.icon_light_mode)


    private var selectedAlias = 0

    var authenticated = false


    var drawerText = ArrayList<String>().also {

        it.add("Manage keys")
        it.add("Import Vault")
        it.add("Export Vault")
        it.add("Help")
        it.add("FAQs")
        it.add("Suggest a feature")
        /*it.add("Support")*/
    }

    //settings : theme - dark , system , light .

    var drawerIcons = ArrayList<Int>().also {
        it.add(R.drawable.icon_dashboard)
        it.add(R.drawable.icon_download)
        it.add(R.drawable.round_arrow_upward_24)
        it.add(R.drawable.twotone_question_mark_24)
        it.add(R.drawable.round_question_answer_24)
        it.add(R.drawable.icon_lightbulb)
        /*   it.add(R.drawable.round_currency_bitcoin_24)*/
    }


    //to edit an pass
    var aliasStr: String = ""
    var titleDecrypted: String = ""
    var userIdDecrypted: String = ""
    var passIdDecrypted = ""
    var desc: String = ""
    private var passwordDecrypted: String = ""

    init {
        viewModelScope.launch(Dispatchers.Default) {
            generateNewPass(4, 5, 5, 2)
        }
    }

    fun copyPassword(context: Context) {

        copy(context, passWord)
        passwordCopyIcon.value = R.drawable.icon_done_24

        viewModelScope.launch {
            delay(1500)
            passwordCopyIcon.value = R.drawable.icon_copy_24
        }


    }

    fun copy(context: Context, content: String) {
        val clipboard =
            context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager

        val clip = ClipData.newPlainText("Copied Text", content)
        clipboard.setPrimaryClip(clip)
    }

    fun generateNewPass(capAlpha: Int, smallAlpha: Int, numbers: Int, symbols: Int): String {

        val genPass = newPass.generateNewPass(
            capAlpha,
            symbols,
            numbers,
            smallAlpha
        )

        passWord = genPass
        return genPass

    }

    fun isLoggedIn(): Boolean {
        auth.currentUser?.reload()
        return auth.currentUser != null
    }

    //miscellaneous
    private fun encryptPass(
        context: Context,
        password: String,
        alias: String,
        error: (String) -> Unit
    ): String? {
        val security = Security(context)
        return security.encryptData(
            password, alias, error
        )
    }


    fun putPassCloud(
        encPass: String,
        passTitle: String,
        passDesc: String,
        passUserId: String,
        context: Context,
        passId: String,
        modify: Long,
        alias: String,
        status: (code: Int, message: String) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        if (auth.currentUser != null) {
            val dbPass =
                db.collection("PasswordManager")
                    .document(auth.currentUser!!.uid)
                    .collection("YourPass").document(passId)

            val data2 =
                PassListEntity(passId, passTitle, passDesc, alias, modify, true)

            dbPass.set(data2, SetOptions.merge()).addOnSuccessListener {
                val passData =
                    PassesEntity(passId, passUserId, encPass, alias)

                db.collection("Passwords")
                    .document(auth.currentUser!!.uid)
                    .collection("YourPass").document(passId).set(passData, SetOptions.merge())
                    .addOnSuccessListener {
                        status(0, "Successfully uploaded to cloud")
                    }.addOnFailureListener {
                        status.invoke(1, it.localizedMessage ?: "Something went wrong")
                        Toast.makeText(
                            context,
                            "Something went wrong",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }.addOnFailureListener {
                Toast.makeText(
                    context,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
                status.invoke(1, it.localizedMessage ?: "Something went wrong")
            }
        } else {
            Toast.makeText(context, "Login First", Toast.LENGTH_SHORT).show()
            status.invoke(1, "Login First")
        }
    }


    fun isMailVerified(): Boolean {
        return auth.currentUser?.isEmailVerified == true
    }

    fun decryptWithAuth(
        context: Context,
        passList: PassListEntity,
        getPass: CoroutineScope.(String) -> Deferred<PassesEntity>,
        message: (msg: String) -> Unit,
        showDialog: () -> Unit
    ) {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (authenticated || !keyguardManager.isDeviceSecure) {
            if (!keyguardManager.isDeviceSecure) {
                message.invoke("Please Protect your device with Screen lock")
            }
            decrypt(
                context = context,
                passList = passList,
                getPass = getPass,
                showDialog = showDialog,
                message = message
            )
        } else {
            authUserWithDevice(context as FragmentActivity, object :
                BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationFailed() {
                    message.invoke("Authentication Failed")
                    stopWorking()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    authenticated = true
                    decrypt(
                        context = context,
                        passList = passList,
                        getPass = getPass,
                        showDialog = showDialog,
                        message = message
                    )
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {

                    when (errorCode) {
                        BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> {
                            message.invoke("Please Secure your device")
                            decrypt(
                                context = context,
                                passList = passList,
                                getPass = getPass,
                                showDialog = showDialog,
                                message = message
                            )
                        }

                        BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                            message.invoke("Secure device with Password or Biometric first")
                            stopWorking()
                        }

                        else -> {
                            message.invoke("Error: $errString")
                            stopWorking()
                        }
                    }
                }
            })

        }
    }

    fun decrypt(
        context: Context,
        passList: PassListEntity,
        getPass: CoroutineScope.(String) -> Deferred<PassesEntity>,
        showDialog: () -> Unit,
        message: (msg: String) -> Unit
    ) {

        viewModelScope.launch(Dispatchers.Default) {
            val passes = getPass(passList.passId).await()
            titleDecrypted =
                passList.title
            userIdDecrypted = passes.userId
            passIdDecrypted = passes.passId
            desc = passList.desc
            aliasStr = passList.alias
            passwordDecrypted = passes.password

            val security = Security(
                context
            )
            decodedData = security.decryptData(passwordDecrypted, aliasStr) { msg ->
                message.invoke(msg)
            }
            viewModelScope.launch(Dispatchers.Main) {
                showDialog()
                stopWorking()
            }
        }

    }

    fun startWorking() {
        working.value = true
    }

    fun stopWorking() {
        working.value = false
    }


    fun createNewPass(
        title: String,
        uid: String,
        desc: String,
        alias: String,
        keySelectedIndex: Int,
        pass: String,
        uploadOnCloud: Boolean,
        context: Context,
        error: (String) -> Unit,
        success: (encPass: String, passId: String, modify: Long, onCloud: Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            selectedAlias = keySelectedIndex
            val encPass = encryptPass(context, pass, alias) {
                error(it)
            }
            if (encPass != null) {
                val modify: Long = System.currentTimeMillis()
                val passId = modify.toString() + title

                if (uploadOnCloud) {
                    putPassCloud(
                        encPass,
                        title,
                        desc,
                        uid,
                        context,
                        passId,
                        modify,
                        alias
                    ) { code, msg ->
                        if (code == 0) {
                            success(encPass, passId, modify, true)
                        } else {
                            error(msg)
                        }
                    }
                } else {
                    success(encPass, passId, modify, false)
                }


            }
        }

    }

    fun deleteSelected(
        passwordsVault: List<PassListEntity>,
        deleteFromDevice: () -> Unit,
        message: (msg: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            var cloudItems = emptyList<Int>()
            cloudItems = cloudItems.toMutableList().apply {
                for (index in selected.value) {
                    if (passwordsVault[index].onCloud) {
                        this.add(index)
                    }
                }
            }
            if (cloudItems.isNotEmpty()) {
                showCloudDel.value = true
                viewModelScope.launch(Dispatchers.IO) {
                    val response =
                        if (isLoggedIn() && isMailVerified()) userInputChannel.receive() else false
                    if (response) {
                        val db = FirebaseFirestore.getInstance()
                        val batch = db.batch()

                        val dbPass =
                            db.collection("PasswordManager")
                                .document(auth.currentUser!!.uid)
                                .collection("YourPass")

                        val dbPasses = db.collection("Passwords")
                            .document(auth.currentUser!!.uid)
                            .collection("YourPass")

                        for (item in cloudItems) {
                            batch.delete(dbPass.document(passwordsVault[item].passId))
                            batch.delete(dbPasses.document(passwordsVault[item].passId))
                        }

                        batch.commit().addOnCompleteListener {
                            if (it.isSuccessful) {
                                message("Deleted from cloud")
                                deleteFromDevice()
                            } else {
                                message("Operation cancelled : Cannot delete from cloud")
                                stopWorking()
                            }
                        }


                    } else {
                        deleteFromDevice()
                        /* getPassesVault()
                         success()*/
                    }
                }
            } else {
                deleteFromDevice()
            }
        }

    }


    // system ->  dark mode -> light mode -> system
    fun toggleTheme(context: Context, isSystemDark: Boolean, message: (msg: String) -> Unit) {
        val sPref = context.getSharedPreferences("app", MODE_PRIVATE)
        var theme = sPref.getBoolean("Theme", false)
        var dark = sPref.getBoolean("dark", false)
        dark = if (theme) {// if system default theme is off
            if (dark) {//if is dark theme
                false
            } else {// if light theme
                true
            }
        } else {
            !isSystemDark
            //themeIcon = R.drawable.
        }
        sPref.edit().also {
            it.putBoolean("Theme", true)
            it.putBoolean("dark", dark)
        }.apply().also {
            message.invoke("Restarting app")
        }
    }


    // system ->  dark mode -> light mode -> system

    fun adjustTheme(context: Context, isSystemDark: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            val spref = context.getSharedPreferences("app", MODE_PRIVATE)
            val theme = spref.getBoolean("Theme", false)
            val dark = spref.getBoolean("dark", false)
            if (theme) {// if system default theme is off
                if (dark) {//if is dark theme
                    themeIcon.value = R.drawable.icon_dark_mode
                } else {// if light theme
                    themeIcon.value = R.drawable.icon_light_mode
                }
            } else {
                if (isSystemDark) {//if is dark theme
                    themeIcon.value = R.drawable.icon_dark_mode
                } else {// if light theme
                    themeIcon.value = R.drawable.icon_light_mode

                }
            }
        }
    }

    private fun authUserWithDevice(
        context: FragmentActivity,
        callback: BiometricPrompt.AuthenticationCallback
    ) {
        val executor = java.util.concurrent.Executors.newSingleThreadExecutor()
        val bioPrompt = BiometricPrompt(context, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            ).build()

        BiometricManager.from(context)
            .canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        bioPrompt.authenticate(promptInfo)

    }

}
