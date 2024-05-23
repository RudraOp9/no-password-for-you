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
import android.text.Editable
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.leo.nopasswordforyou.R
import com.leo.nopasswordforyou.databinding.ActivityShowPassBinding
import com.leo.nopasswordforyou.helper.ItemClickListner
import com.leo.nopasswordforyou.helper.PassAdapter
import com.leo.nopasswordforyou.helper.PassAdapterData
import com.leo.nopasswordforyou.helper.checkExternalWritePer
import com.leo.nopasswordforyou.secuirity.Security
import java.io.IOException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.UnrecoverableEntryException
import java.security.cert.CertificateException
import java.security.spec.InvalidKeySpecException
import java.util.Objects
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException

class ShowPass : AppCompatActivity(), ItemClickListner {
    lateinit var db: FirebaseFirestore
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var dbTitles: CollectionReference
    lateinit var dbPass: DocumentReference
    lateinit var passData: ArrayList<PassAdapterData>
    lateinit var passAdapter: PassAdapter
    lateinit var alertDialog1: AlertDialog
    lateinit var keySetting: AlertDialog
    lateinit var keySettingNewKey: AlertDialog

    lateinit var binding: ActivityShowPassBinding
    val requestPermissionLauncher =
        this.registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { _: Boolean ->

        }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowPassBinding.inflate(
            layoutInflater
        )
        val view: View = binding.root
        setContentView(view)



        alertDialog1 = MaterialAlertDialogBuilder(this).setView(R.layout.loading_dilogue_2).create()
        alertDialog1.setCanceledOnTouchOutside(false)
        alertDialog1.setCancelable(false)
        alertDialog1.show()
        val t = alertDialog1.findViewById<TextView>(R.id.loadingText)
        if (t != null) {
            t.text = "Getting passwords"
        }

        passData = ArrayList()


        auth.addAuthStateListener(object : AuthStateListener {
            override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                if (firebaseAuth.currentUser == null) {
                    auth.removeAuthStateListener(this)
                    finish()
                }
            }
        })

        if (auth.currentUser == null) {
            startActivity(Intent(this, login_page::class.java))
            Toast.makeText(this, "Login first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db = FirebaseFirestore.getInstance()
        dbTitles = db.collection("PasswordManager").document(auth.currentUser!!.uid)
            .collection("YourPass")
        passAdapter = PassAdapter(passData, auth.currentUser!!.uid, db)
        passAdapter.setClickListener(this)
        binding.rvPasses.adapter = passAdapter
        binding.rvPasses.layoutManager = LinearLayoutManager(this)
        getPasses(Source.CACHE)


        binding.syncPass.setOnClickListener { v: View? -> getPasses(Source.SERVER) }
        binding.keySet.setOnClickListener { v: View? ->
            keySetting = MaterialAlertDialogBuilder(this).setView(R.layout.custom_keyset).create()
            keySetting.setCanceledOnTouchOutside(true)
            keySetting.setCancelable(true)
            keySetting.show()
            val newKey = keySetting.findViewById<MaterialButton>(R.id.newKey)
            val addKey = keySetting.findViewById<MaterialButton>(R.id.addKey)
            addKey?.setOnClickListener { v1: View? ->

            }
            newKey?.setOnClickListener { v12: View? ->
                keySettingNewKey =
                    MaterialAlertDialogBuilder(this).setView(R.layout.add_alias).create()
                keySettingNewKey.setCanceledOnTouchOutside(true)
                keySettingNewKey.setCancelable(true)
                keySettingNewKey.show()
                val text = keySettingNewKey.findViewById<TextInputEditText>(R.id.alias)
                val doneButton = keySettingNewKey.findViewById<AppCompatButton>(R.id.addKey)
                val infoText = keySettingNewKey.findViewById<TextView>(R.id.infoText)

                var exit = false
                doneButton?.setOnClickListener {
                    if (exit) {
                        keySettingNewKey.dismiss()
                        keySetting.dismiss()
                    } else if (text != null && checkExternalWritePer(
                            this,
                            this,
                            requestPermissionLauncher
                        )
                    ) {
                        if (text.text.toString().isNotEmpty()) {
                            val security = Security(
                                this, text.text.toString()
                            )
                            val result = security.newKey()
                            if (result.equals("done")) {
                                doneButton.text = "Exit"
                                exit = true
                                infoText?.text =
                                    "An new Key with the alias : ${text.text.toString()} has been created and has been saved to \n 'Download/noPassWordForYou/${text.text.toString()}.ppk' \n Never Share the file to someone else and keep it private.\n why ? see Help section for More Info !"

                            }
                            Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                        } else Toast.makeText(this, "empty alias", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getPasses(source: Source) {
        if (!alertDialog1.isShowing) {
            alertDialog1.show()
        }
        passData.clear()
        dbTitles[source].addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot? ->
            if (queryDocumentSnapshots != null) {
                for (a in queryDocumentSnapshots.documents) {
                    passData.add(
                        PassAdapterData(
                            a["Title"] as String?,
                            a["Desc"] as String?,
                            a["id"] as String?
                        )
                    )
                }
                alertDialog1.dismiss()
                passAdapter.notifyDataSetChanged()
            }
        }.addOnFailureListener { e: Exception ->
            Toast.makeText(this@ShowPass, e.message, Toast.LENGTH_SHORT).show()
            alertDialog1.dismiss()
        }
    }

    override fun onClick(v: View, id: String, Title: String, Desc: String) {
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
                val decodedData = security.decryptData(ToDecode) {
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
    }


    fun copy(text: String?) {
        val clipboard = this@ShowPass.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
    }
}
