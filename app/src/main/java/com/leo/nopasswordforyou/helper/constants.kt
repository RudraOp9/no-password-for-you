/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 05/03/24, 12:00 pm
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

package com.leo.nopasswordforyou.helper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class constant(id: String, authId: String) {
    var documentRefPassInfo =
        FirebaseFirestore.getInstance().collection(TODO()).document(authId).collection("YourPass")
            .document(id)
    var documentRefPassLocation =
        FirebaseFirestore.getInstance().collection(TODO()).document(TODO()).collection(TODO())
            .document(TODO())
}


/*

var collection = "none"
var document = "none"
var subCollection = "none"
var subDocument = "none"
*/


/*var passRef = FirebaseFirestore.getInstance().collection(collection).document(document).collection(
    subCollection).document(subDocument)*/

