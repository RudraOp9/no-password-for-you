/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 10/03/24, 7:12 pm
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

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.leo.nopasswordforyou.R
import com.leo.nopasswordforyou.databinding.ActivityGeneratePassBinding
import com.leo.nopasswordforyou.secuirity.Security
import com.leo.nopasswordforyou.viewmodel.GeneratePassVM
import java.util.Objects


class GeneratePass : AppCompatActivity() {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    lateinit var security: Security
    lateinit var vm: GeneratePassVM
    private lateinit var binding: ActivityGeneratePassBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeneratePassBinding.inflate(
            layoutInflater
        )
        val view: View = binding!!.root
        setContentView(view)
        vm = ViewModelProvider(this).get(GeneratePassVM::class.java)

        vm.passWord.observe(this) { s: String? ->
            binding.passText.setText(s)
        }

        vm.total.observe(this) { s: String? ->
            binding.total.text = s
        }


        //TODO make an app for the ads on billboard etc...
        security = if (auth.currentUser == null) {
            Security(this, "NoPassWordForTheNewUser")
        } else Security(
            this,
            ""
        )


        binding.copyPassEnc.setOnClickListener {

            val copy: String? = security.encryptData(
                binding.passText.text.toString()
            ) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
            if (copy != null) {


                val clipboard =
                    this@GeneratePass.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied Text", copy)
                clipboard.setPrimaryClip(clip)
                //    Snackbar.make(v, "This button is for this release only \n it will be removed in beta ++ releases", 3000);
                Toast.makeText(this, "copied", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        binding.regeneratePass.setOnClickListener { v: View? ->
            vm.genNewPass()
        }

        binding.saveToCloud.setOnClickListener { v: View? ->
            if (auth.currentUser == null) {
                Toast.makeText(this, "Login First !", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val alertDialog =
                MaterialAlertDialogBuilder(this).setView(R.layout.custom_save_to_cloud).create()
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.show()

            val newPassCustom = alertDialog.findViewById<MaterialButton>(R.id.newPassCustom)
            val passSaveCustom = alertDialog.findViewById<AppCompatEditText>(R.id.passSaveCustom)

            if (passSaveCustom != null) {
                passSaveCustom.visibility = View.GONE
            }
            if (newPassCustom != null) {
                newPassCustom.visibility = View.GONE
            }

            val passTitleCustom = alertDialog.findViewById<AppCompatEditText>(R.id.passTitleCustom)
            val passUserIdCustom =
                alertDialog.findViewById<AppCompatEditText>(R.id.passUserIdCustom)
            val passDescCustom = alertDialog.findViewById<AppCompatEditText>(R.id.passDescCustom)
            val passDoneCustom = alertDialog.findViewById<FloatingActionButton>(R.id.passDoneCustom)
            val exitButtonCustom =
                alertDialog.findViewById<FloatingActionButton>(R.id.exitButtonCustom)

            exitButtonCustom?.setOnClickListener { v12: View? -> alertDialog.dismiss() }
            if (passDoneCustom != null) {
                passDoneCustom.setOnClickListener(View.OnClickListener { v1: View? ->
                    if (Objects.requireNonNull<Editable?>(
                            Objects.requireNonNull<AppCompatEditText?>(passTitleCustom).text
                        )
                            .toString().isEmpty()
                    ) {
                        Snackbar.make(v1!!, "Empty title", 2000).show()
                    } else {
                        val alertDialog1 =
                            MaterialAlertDialogBuilder(this).setView(R.layout.loading_dilogue_2)
                                .create()
                        alertDialog1.setCanceledOnTouchOutside(false)
                        alertDialog1.setCancelable(false)
                        alertDialog1.show()
                        val t = alertDialog1.findViewById<TextView>(R.id.loadingText)
                        if (t != null) {
                            t.text = "secure uploading"
                        }
                        //   Snackbar.make(vie, "Uploading", 2000).show();
                        val passTitle = passTitleCustom!!.text.toString()
                        var passDesc = "empty"
                        var passUserId = "empty"
                        if (passUserIdCustom != null) {
                            passUserId = Objects.requireNonNull(passUserIdCustom.text).toString()
                        }
                        if (passDescCustom != null) {
                            passDesc = Objects.requireNonNull(passDescCustom.text).toString()
                        }

                        alertDialog.dismiss()


                        val encPass: String? = security.encryptData(
                            binding.passText.text.toString()
                        ) {
                            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                            alertDialog1.dismiss()
                        }

                        if (encPass != null) {
                            val db = FirebaseFirestore.getInstance()
                            if (auth.currentUser != null) {
                                val id = System.currentTimeMillis().toString()
                                val dbPass =
                                    db.collection("PasswordManager")
                                        .document(auth.currentUser!!.uid)
                                        .collection("YourPass").document(id + passTitle)

                                val data: MutableMap<String, String> = HashMap()
                                data["Title"] = passTitle
                                data["Desc"] = passDesc
                                data["id"] = id + passTitle
                                val finalPassUserId = passUserId
                                dbPass.set(data).addOnSuccessListener { documentReference: Void? ->
                                    data.clear()
                                    data["pass"] = encPass
                                    data["UserId"] = finalPassUserId
                                    db.collection("Passwords")
                                        .document(auth.currentUser!!.uid)
                                        .collection("YourPass").document(id + passTitle).set(data)
                                        .addOnSuccessListener { unused: Void? ->
                                            Toast.makeText(
                                                this@GeneratePass,
                                                "Successfully completed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            alertDialog1.dismiss()
                                        }.addOnFailureListener { e: Exception? ->
                                            alertDialog1.dismiss()
                                            Toast.makeText(
                                                this@GeneratePass,
                                                "Something went wrong",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }.addOnFailureListener { e: Exception? ->
                                    Toast.makeText(
                                        this@GeneratePass,
                                        "Something went wrong",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    alertDialog1.dismiss()
                                }
                            } else {
                                Toast.makeText(this, "Login First", Toast.LENGTH_SHORT).show()
                                alertDialog1.dismiss()
                            }
                        }
                    }
                })
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }
        }

        binding.copyPass.setOnClickListener {
            val clipboard =
                this@GeneratePass.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", binding.passText.text)
            clipboard.setPrimaryClip(clip)
            val handler = Handler()
            binding.copyPass.setImageResource(R.drawable.icon_done_24)
            //    copyPass.setBackgroundResource(R.drawable.icon_done_24);
            handler.postDelayed(
                { binding.copyPass.setImageResource(R.drawable.icon_copy_24) },
                1500
            )
        }


        val values = arrayOf("10", "9", "8", "7", "6", "5", "4", "3", "2", "1", "0")

        val adapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            values
        )


        binding.spinnerCapLetter.adapter = adapter
        binding.spinnerNumbers.adapter = adapter
        binding.spinnerSmallLetter.adapter = adapter
        binding.spinnerSpecialSym.adapter = adapter
        binding.spinnerCapLetter.setSelection(4)
        binding.spinnerNumbers.setSelection(4)
        binding.spinnerSmallLetter.setSelection(6)
        binding.spinnerSpecialSym.setSelection(2)


        binding.spinnerNumbers.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    vm.numberslen = values[position].toByte()

                    vm.updateTotalText()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    vm.numberslen = 4.toByte()
                    vm.updateTotalText()
                }
            }
        binding.spinnerCapLetter.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    vm.alphaCapLength = values[position].toByte()
                    vm.updateTotalText()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    vm.alphaCapLength = 4.toByte()
                    vm.updateTotalText()
                }
            }
        binding.spinnerSmallLetter.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    vm.alphaSmallLength = values[position].toByte()
                    vm.updateTotalText()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    vm.numberslen = 6.toByte()
                    vm.updateTotalText()
                }
            }
        binding.spinnerSpecialSym.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    vm.specialSymbol = values[position].toByte()
                    vm.updateTotalText()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    vm.numberslen = 2.toByte()

                    vm.updateTotalText()
                }
            }
        binding.customSetSwitch.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                binding.customSettings.visibility = View.VISIBLE
                vm.numberslen = (binding.spinnerNumbers.selectedItem as String).toByte()
                vm.alphaCapLength = (binding.spinnerCapLetter.selectedItem as String).toByte()
                vm.alphaSmallLength =
                    (binding.spinnerSmallLetter.selectedItem as String).toByte()
                vm.specialSymbol = (binding.spinnerSpecialSym.selectedItem as String).toByte()
            } else {
                binding.customSettings.visibility = View.GONE
                vm.numberslen = 4.toByte()
                vm.alphaCapLength = 4.toByte()
                vm.alphaSmallLength = 6.toByte()
                vm.specialSymbol = 2.toByte()
                vm.passLength = 16.toByte()
            }
        }
    }
}