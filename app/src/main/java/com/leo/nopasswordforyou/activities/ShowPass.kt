/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 20/05/24, 6:58 pm
 *  Copyright (c) 2024 . All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.leo.nopasswordforyou.activities

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.leo.nopasswordforyou.R
import com.leo.nopasswordforyou.components.CreateNewItem
import com.leo.nopasswordforyou.database.passes.PassesEntity
import com.leo.nopasswordforyou.database.passlist.PassListEntity
import com.leo.nopasswordforyou.databinding.ActivityShowPassBinding
import com.leo.nopasswordforyou.databinding.CustomSaveToCloudBinding
import com.leo.nopasswordforyou.databinding.CustomShowPassBinding
import com.leo.nopasswordforyou.secuirity.Security
import com.leo.nopasswordforyou.viewmodel.ShowPassVM
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ShowPass : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var dbTitles: CollectionReference
    private lateinit var dbPass: DocumentReference
    /*    private lateinit var passData: ArrayList<PassAdapterData>*/
    /*   private lateinit var passAdapter: PassAdapter*/
    /*   private lateinit var alertDialog1: AlertDialog*/

    /*    private lateinit var keySettingNewKey: AlertDialog*/
    private lateinit var vm: ShowPassVM
    private lateinit var binding: ActivityShowPassBinding
    private val requestPermissionLauncher =
        this.registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { _: Boolean ->

        }
    private val filePickerLauncher =
        this.registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it != null) {
                contentResolver.openInputStream(it)?.use { inputStream ->

                    val fileContents = inputStream.bufferedReader()
                        .use { it0 -> it0.readText() }
                    Security(this).importKey(fileContents) { result ->
                        if (result != "error") {
                            Snackbar.make(binding.root, "Key successfully imported", 3500).show()
                            vm.setAlias(result)
                        } else {
                            Snackbar.make(binding.root, "Error , contact support", 3500).show()
                        }
                    }
                }
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth.addAuthStateListener(object : AuthStateListener {
            override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                if (firebaseAuth.currentUser == null) {
                    auth.removeAuthStateListener(this)
                    finish()
                }
            }
        })

        binding = ActivityShowPassBinding.inflate(
            layoutInflater
        )
        val view: View = binding.root
        setContentView(view)

        vm = ViewModelProvider(this)[ShowPassVM::class.java]




        binding.composeShowPass.setContent {
            val context = LocalContext.current
            var showUpdateItem by rememberSaveable {
                mutableStateOf(false)
            }
            var updateItem by rememberSaveable {
                mutableStateOf(false)
            }
            Column {
                TopAppBar(title = { Text(text = "Passwords") })

                Box(modifier = Modifier) {
                    LazyColumn {
                        items(vm.passwords.value.size) {
                            Card(
                                onClick = {
                                    vm.getPass(vm.passwords.value[it].id) { pass ->
                                        vm.titleStr = vm.passwords.value[it].title
                                        vm.uid = pass.userId
                                        vm.id = pass.passId
                                        vm.desc = vm.passwords.value[it].description
                                        vm.aliasStr = vm.passwords.value[it].alias
                                        vm.passwordStr = pass.password
                                        showUpdateItem = true
                                    }


                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 10.dp)
                            ) {
                                //Text(text = "this is an card.." , modifier = Modifier.padding(50.dp))
                                Text(
                                    text = vm.passwords.value[it].title,
                                    fontSize = 17.sp,
                                    fontFamily = FontFamily(
                                        Font(
                                            R.font.open_sans,
                                            FontWeight.Normal
                                        )
                                    ),
                                    modifier = Modifier.padding(
                                        start = 10.dp,
                                        top = 10.dp,
                                        end = 10.dp
                                    )
                                )
                                Text(
                                    text = vm.passwords.value[it].description,
                                    fontFamily = FontFamily(
                                        Font(
                                            R.font.open_sans,
                                            FontWeight.Normal
                                        )
                                    ),
                                    modifier = Modifier.padding(
                                        start = 10.dp,
                                        top = 5.dp,
                                        end = 10.dp,
                                        bottom = 10.dp
                                    )
                                )

                            }
                        }
                    }
                    if (showUpdateItem) {
                        val security = Security(
                            context
                        )
                        val decodedData = security.decryptData(vm.passwordStr, vm.aliasStr) {
                            Snackbar.make(view, it, 3000).show()
                        }
                        if (decodedData != null) {
                            com.leo.nopasswordforyou.components.ShowPass(
                                id = vm.uid,
                                password = decodedData,
                                description = vm.desc,
                                dismissDialog = { showUpdateItem = false },
                                copy = {
                                    val clipboard =
                                        context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Copied Text", it)
                                    clipboard.setPrimaryClip(clip)
                                }

                            ) {
                                updateItem = true
                            }
                        }


                        if (updateItem) {
                            CreateNewItem(
                                arrayKeys = null,
                                alias0 = vm.aliasStr,
                                title0 = vm.titleStr,
                                uid0 = vm.uid,
                                desc0 = vm.desc,
                                password0 = vm.passwordStr,
                                isUpdate = true,
                                onExit = { updateItem = false }) { title, uid, desc, alias, _ ->
                                if (title.isEmpty()) {
                                    Snackbar.make(this as View, "Title is Required", 3000).show()
                                } else {
                                    Snackbar.make(view, "updating please wait ...", 4000).show()
                                    vm.updatePassList(
                                        PassListEntity(
                                            passId = vm.id,
                                            title = title,
                                            desc = desc,
                                            alias = alias,
                                            lastModify = System.currentTimeMillis()
                                        )
                                    )
                                    vm.updatePass(
                                        PassesEntity(
                                            passId = vm.id,
                                            userId = uid,
                                            password = vm.passwordStr,
                                            alias = alias
                                        )
                                    )
                                    vm.getPasses() {
                                        updateItem = false
                                        showUpdateItem = false
                                    }

                                }
                            }
                        }
                    }
                }

            }
        }


        /*  val lDB = LoadingDilogue2Binding.inflate(layoutInflater)
          alertDialog1 = alertDialogueBuilder(lDB.root)

          lDB.loadingText.text = "Getting passwords"*/

        /*   passData = ArrayList()*/


        /*db = FirebaseFirestore.getInstance()
        dbTitles = db.collection("PasswordManager").document(auth.currentUser!!.uid)
            .collection("YourPass")
        passAdapter = PassAdapter(passData, auth.currentUser!!.uid, db)
        passAdapter.setClickListener(this)*/
        /* binding.rvPasses.adapter = passAdapter
         binding.rvPasses.layoutManager = LinearLayoutManager(this)*/


        /*   binding.syncPass.setOnClickListener { getPasses() }*/

        /*  binding.keySet.setOnClickListener {

              val keyBind = CustomKeysetBinding.inflate(layoutInflater)


              val keySetting: AlertDialog = alertDialogueBuilder(keyBind.root)
              keyBind.addKey.setOnClickListener {
                 // filePickerLauncher.launch(arrayOf("*/
        /*"))
            }
            keyBind.newKey.setOnClickListener {
                val ksnBind = AddAliasBinding.inflate(layoutInflater)
                keySettingNewKey = alertDialogueBuilder(ksnBind.root)


                var exit = false
                ksnBind.addKey.setOnClickListener {
                    if (exit) {
                        keySettingNewKey.dismiss()
                        keySetting.dismiss()
                    } else if (checkExternalWritePer(
                            this,
                            requestPermissionLauncher
                        )
                    ) {
                        if (ksnBind.alias.text.toString().trim().isNotEmpty()) {
                            val security = Security(
                                this
                            )
                            val result = security.newKey(ksnBind.alias.text.toString())
                            if (result == "done") {
                                ksnBind.addKey.text = "Exit"
                                exit = true
                                vm.setAlias(ksnBind.alias.text.toString())
                                ksnBind.infoText.text =
                                    "An new Key with the alias : ${ksnBind.alias.text.toString()} has been created and has been saved to \n'Download/noPassWordForYou/${ksnBind.alias.text.toString()}.ppk' \nNever Share the file to someone else and keep it private.\nSee Help section for More Info !"
                            }
                            Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                        } else Toast.makeText(this, "empty alias", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }*/
    }


    fun onClick(id: String, Title: String, Desc: String, alias: String) {
//        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        if (auth.currentUser == null) {
            startActivity(Intent(this, login_page::class.java))
            Toast.makeText(this, "Login first", Toast.LENGTH_SHORT).show()
            finish()
        }
        //val dbPass3 = dbTitles.document(id)


        vm.getPass(id) {
            val ToDecode = it.password
            val UserId = it.userId
            val security = Security(
                this
            )
            val decodedData = security.decryptData(ToDecode, it.alias) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }


            if (decodedData != null) {
                Log.d("tag", decodedData)

                val uB = CustomShowPassBinding.inflate(layoutInflater)
                val alertDialog1 = alertDialogueBuilder(uB.root)
                //MaterialAlertDialogBuilder(this).setView(R.layout.custom_show_pass).create()
                /*alertDialog1.setCanceledOnTouchOutside(false)
                alertDialog1.setCancelable(true)
                alertDialog1.show()*/

                val passShowCustom =
                    alertDialog1.findViewById<MaterialTextView>(R.id.passShowCustom)
                val passUserIdShowCustom =
                    alertDialog1.findViewById<MaterialTextView>(R.id.passUserIdShowCustom)
                val showPassEyeCustom =
                    alertDialog1.findViewById<FloatingActionButton>(R.id.showPassEyeCustom)
                val copyPassCustom =
                    alertDialog1.findViewById<MaterialButton>(R.id.copyPassCustom)
                val copyUserIdCustom =
                    alertDialog1.findViewById<MaterialButton>(R.id.copyUserIdCustom)
                val passTool = alertDialog1.findViewById<AppCompatImageView>(R.id.passTool)


                // delete of update
                passTool?.setOnClickListener {
                    val ad = AlertDialog.Builder(this@ShowPass)
                        .setTitle("Alert !")
                        .setMessage("You are changing your password Fields.")
                        .setCancelable(true)
                        .setPositiveButton("Update") { dialog: DialogInterface, _: Int ->

                            val uBSave = CustomSaveToCloudBinding.inflate(layoutInflater)
                            val alertDialog2 = alertDialogueBuilder(uBSave.root)

                            uBSave.passSaveCustom.setText(ToDecode)
                            uBSave.passTitleCustom.setText(Title)
                            uBSave.passDescCustom.setText(Desc)
                            uBSave.passUserIdCustom.setText(UserId)
                            uBSave.newPassCustom.setOnClickListener { _: View? ->
                                Toast.makeText(
                                    this,
                                    "Copy the Encoded PassWord",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this@ShowPass, GeneratePass::class.java))
                            }


                            //: MAKE CHANGES IN SHOW PASS.CLASS FOR NEW PASS CUSTOM.
                            uBSave.exitButtonCustom.setOnClickListener { _: View? -> alertDialog2.dismiss() }

                            // pass done custom
                            uBSave.passDoneCustom.setOnClickListener { _: View? ->
                                val dbPass2 =
                                    db.collection("PasswordManager")
                                        .document(auth.currentUser!!.uid)
                                        .collection("YourPass")
                                        .document(id)
                                Toast.makeText(this, "Working", Toast.LENGTH_SHORT).show()
                                val passListData = PassListEntity(
                                    id,
                                    uBSave.passTitleCustom.text.toString(),
                                    uBSave.passDescCustom.text.toString(),
                                    alias,
                                    System.currentTimeMillis()
                                )

                                dbPass2.set(passListData, SetOptions.merge())
                                    .addOnSuccessListener {
                                        vm.updatePassList(passListData)

                                        val passData = PassesEntity(
                                            id,
                                            uBSave.passUserIdCustom.text.toString(),
                                            uBSave.passSaveCustom.text.toString(),
                                            alias
                                        )
                                        vm.updatePass(
                                            passData
                                        )
                                        dbPass =
                                            db.collection("Passwords")
                                                .document(auth.currentUser!!.uid)
                                                .collection("YourPass")
                                                .document(id)
                                        dbPass.set(passData, SetOptions.merge())
                                            .addOnFailureListener { e: Exception ->
                                                Toast.makeText(
                                                    this@ShowPass, e.message, Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                    }.addOnFailureListener { e: Exception ->
                                        Toast.makeText(
                                            this@ShowPass, e.message, Toast.LENGTH_SHORT
                                        ).show()
                                    }



                                alertDialog2.dismiss()
                                vm.getPasses()
                                Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show()
                            }


                            dialog.cancel()
                            alertDialog1.dismiss()
                        }
                        .setNegativeButton("Delete") { dialog: DialogInterface, _: Int ->
                            Log.d("tag", "id : $id Title : $Title")
                            vm.deletePass(id)
                            vm.deletePassList(id)
                            alertDialog1.dismiss()
                            dialog.dismiss()
                            vm.getPasses()
                        }
                    ad.create().show()
                }

                if (passShowCustom != null) {
                    passShowCustom.text = decodedData

                    if (showPassEyeCustom != null) {
                        val a = booleanArrayOf(true)
                        showPassEyeCustom.setOnClickListener(View.OnClickListener {
                            if (a[0]) {
                                passShowCustom.inputType =
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                a[0] = false
                            } else {
                                a[0] = true
                                passShowCustom.inputType =
                                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            }
                        })
                    }
                }

                copyUserIdCustom?.setOnClickListener { copy(UserId) }

                copyPassCustom?.setOnClickListener {
                    copy(decodedData)
                }
                if (passUserIdShowCustom != null) {
                    passUserIdShowCustom.text = UserId
                }
            }
        }
    }

    fun copy(text: String?) {
        val clipboard = this@ShowPass.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun alertDialogueBuilder(view: View): AlertDialog {
        val a = MaterialAlertDialogBuilder(this).setView(view).create()
        a.setCancelable(true)
        a.setCanceledOnTouchOutside(true)
        a.show()
        return a
    }


}
/*override fun onClick(v: View, id: String, Title: String, Desc: String) {
//        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        if (auth.currentUser == null) {
            startActivity(Intent(this, login_page::class.java))
            Toast.makeText(this, "Login first", Toast.LENGTH_SHORT).show()
            finish()
        }
        val dbPass3 = dbTitles.document(id)
        dbPass =
            db.collection("Passwords")
                .document(auth.currentUser!!.uid)
                .collection("YourPass")
                .document(id)

        dbPass.get().addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
            if (documentSnapshot.data == null) {
                Snackbar.make(v, "something went wrong", 2000).show()
            } else {
                val ToDecode = documentSnapshot["pass"] as String?
                val UserId = documentSnapshot["UserId"] as String?
                val security = Security(
                    this,
                    "NOPASSWORDFF!!!!" + (FirebaseAuth.getInstance().currentUser?.uid)
                )
                val decodedData = security.decryptData(ToDecode, "") {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }

                if (decodedData != null) {
                Log.d("tag", decodedData)

                val alertDialog1 =
                    MaterialAlertDialogBuilder(this).setView(R.layout.custom_show_pass).create()
                alertDialog1.setCanceledOnTouchOutside(false)
                alertDialog1.setCancelable(true)
                alertDialog1.show()

                val passShowCustom =
                    alertDialog1.findViewById<MaterialTextView>(R.id.passShowCustom)
                val passUserIdShowCustom =
                    alertDialog1.findViewById<MaterialTextView>(R.id.passUserIdShowCustom)
                val showPassEyeCustom =
                    alertDialog1.findViewById<FloatingActionButton>(R.id.showPassEyeCustom)
                val copyPassCustom = alertDialog1.findViewById<MaterialButton>(R.id.copyPassCustom)
                val copyUserIdCustom =
                    alertDialog1.findViewById<MaterialButton>(R.id.copyUserIdCustom)
                val passTool = alertDialog1.findViewById<AppCompatImageView>(R.id.passTool)


                // delete of update
                passTool?.setOnClickListener { v14: View? ->
                    val ad = AlertDialog.Builder(this@ShowPass)
                        .setTitle("Alert !")
                        .setMessage("You are changing your password Fields.")
                        .setCancelable(true)
                        .setPositiveButton("Update") { dialog: DialogInterface, which: Int ->
                            val alertDialog2 = MaterialAlertDialogBuilder(
                                this
                            ).setView(R.layout.custom_save_to_cloud).create()
                            alertDialog2.setCanceledOnTouchOutside(false)
                            alertDialog2.setCancelable(true)
                            alertDialog2.show()


                            alertDialog2.show()
                            val passSaveCustom =
                                alertDialog2.findViewById<AppCompatEditText>(R.id.passSaveCustom)
                            val passTitleCustom =
                                alertDialog2.findViewById<AppCompatEditText>(R.id.passTitleCustom)
                            val passUserIdCustom =
                                alertDialog2.findViewById<AppCompatEditText>(R.id.passUserIdCustom)
                            val passDescCustom =
                                alertDialog2.findViewById<AppCompatEditText>(R.id.passDescCustom)
                            val passDoneCustom =
                                alertDialog2.findViewById<FloatingActionButton>(R.id.passDoneCustom)
                            val exitButtonCustom =
                                alertDialog2.findViewById<FloatingActionButton>(R.id.exitButtonCustom)
                            val newPassCustom =
                                alertDialog2.findViewById<MaterialButton>(R.id.newPassCustom)

                            if (!(passSaveCustom != null && passUserIdCustom != null && passTitleCustom != null && passDescCustom != null && newPassCustom != null && exitButtonCustom != null && passDoneCustom != null)) {
                                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT)
                                    .show()
                                alertDialog2.dismiss()
                                return@setPositiveButton
                            }
                            passSaveCustom.setText(ToDecode)
                            passTitleCustom.setText(Title)
                            passDescCustom.setText(Desc)
                            passUserIdCustom.setText(UserId)
                            newPassCustom.setOnClickListener(View.OnClickListener { v15: View? ->
                                Toast.makeText(
                                    this,
                                    "Copy the Encoded PassWord",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this@ShowPass, GeneratePass::class.java))
                            })


                            //: MAKE CHANGES IN SHOW PASS.CLASS FOR NEW PASS CUSTOM.
                            exitButtonCustom.setOnClickListener(View.OnClickListener { v12: View? -> alertDialog2.dismiss() })

                            // pass done custom
                            passDoneCustom.setOnClickListener(View.OnClickListener { v16: View? ->
                                val dbPass2 =
                                    db.collection("PasswordManager")
                                        .document(auth.currentUser!!.uid)
                                        .collection("YourPass")
                                        .document(id)
                                Toast.makeText(this, "Working", Toast.LENGTH_SHORT).show()
                                val data: MutableMap<String, String> = HashMap()

                                if (Objects.requireNonNull<Editable?>(passTitleCustom.text)
                                        .toString().trim { it <= ' ' } != Title
                                ) {
                                    data["Title"] = passTitleCustom.text.toString()
                                }


                                if (Objects.requireNonNull<Editable?>(passDescCustom.text)
                                        .toString().trim { it <= ' ' } != Desc
                                ) {
                                    data["Desc"] = passDescCustom.text.toString()
                                }
                                if ((data.size != 0)) {
                                    dbPass2.set(data, SetOptions.merge())
                                        .addOnFailureListener { e: Exception ->
                                            Toast.makeText(
                                                this@ShowPass, e.message, Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                                data.clear()

                                if (ToDecode != null && ToDecode != Objects.requireNonNull<Editable?>(
                                        passSaveCustom.text
                                    ).toString() && Objects.requireNonNull<Editable?>(
                                        passSaveCustom.text
                                    ).toString().length > 50
                                ) {
                                    data["pass"] = passSaveCustom.text.toString()
                                }

                                if (Objects.requireNonNull<Editable?>(passUserIdCustom.text)
                                        .toString() != UserId
                                ) {
                                    data["UserId"] = passUserIdCustom.text.toString()
                                }
                                if (data.isNotEmpty()) {
                                    dbPass2.set(data, SetOptions.merge())
                                        .addOnFailureListener { e: Exception ->
                                            Toast.makeText(
                                                this@ShowPass, e.message, Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                                alertDialog2.dismiss()
                                Toast.makeText(this, "updated", Toast.LENGTH_SHORT).show()
                            })


                            dialog.cancel()
                            alertDialog1.dismiss()
                        }
                        .setNegativeButton("Delete") { dialog: DialogInterface, which: Int ->
                            Log.d("tag", "id : $id Title : $Title")
                            dbPass.delete()
                            dbPass3.delete().addOnSuccessListener { unused: Void? ->
                                Log.d(
                                    "tag",
                                    " delete it"
                                )
                            }
                                .addOnFailureListener { e: Exception ->
                                    Log.d("tag", "can't delete it")
                                    Toast.makeText(
                                        this@ShowPass,
                                        e.message + " Localized : " + e.localizedMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            alertDialog1.dismiss()
                            getPasses(Source.SERVER)
                            dialog.dismiss()
                        }
                    ad.create().show()
                }

                if (passShowCustom != null) {
                    passShowCustom.text = decodedData

                    if (showPassEyeCustom != null) {
                        val a = booleanArrayOf(true)
                        showPassEyeCustom.setOnClickListener(View.OnClickListener { v1: View? ->
                            if (a[0]) {
                                passShowCustom.inputType =
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                a[0] = false
                            } else {
                                a[0] = true
                                passShowCustom.inputType =
                                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            }
                        })
                    }
                }

                copyUserIdCustom?.setOnClickListener { copy(UserId) }
                if (copyPassCustom != null) {
                    val finalDecodedData = decodedData
                    copyPassCustom.setOnClickListener(View.OnClickListener {
                        copy(finalDecodedData)
                    })
                }
                if (passUserIdShowCustom != null) {
                    passUserIdShowCustom.text = UserId
                }
                }
            }
        }.addOnFailureListener { e: Exception ->
            Toast.makeText(
                this,
                e.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }}.addOnFailureListener {
    e: Exception ->
    Toast.makeText(
        this,
        e.message,
        Toast.LENGTH_SHORT
    ).show()
}
}*/