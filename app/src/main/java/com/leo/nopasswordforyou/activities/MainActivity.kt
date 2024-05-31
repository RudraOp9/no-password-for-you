/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 21/02/24, 6:44 pm
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

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.leo.nopasswordforyou.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)


        binding!!.generatePass.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity, GeneratePass::class.java
                )
            )
        }
        binding!!.activityLogin.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity, login_page::class.java
                )
            )
        }
    }
}