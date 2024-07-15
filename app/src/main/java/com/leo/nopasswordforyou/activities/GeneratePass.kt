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

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.leo.nopasswordforyou.databinding.ActivityGeneratePassBinding
import com.leo.nopasswordforyou.screen.GeneratePass
import com.leo.nopasswordforyou.viewmodel.GeneratePassVM
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GeneratePass : AppCompatActivity() {

    lateinit var vm: GeneratePassVM
    private lateinit var binding: ActivityGeneratePassBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeneratePassBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        vm = ViewModelProvider(this).get(GeneratePassVM::class.java)
        binding.composeGeneratePass.setContent {
            GeneratePass(vm)
        }

        //TODO make an app for the ads on billboard etc...

        /*   val values = arrayOf("10", "9", "8", "7", "6", "5", "4", "3", "2", "1", "0")

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
   */

        /*    binding.spinnerNumbers.onItemSelectedListener =
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
                }*/
        /*   binding.spinnerCapLetter.onItemSelectedListener =
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
               }*/
        /*  binding.spinnerSmallLetter.onItemSelectedListener =
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
              }*/
        /*  binding.spinnerSpecialSym.onItemSelectedListener =
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
              }*/
        /*     binding.customSetSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
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
             }*/
    }
}