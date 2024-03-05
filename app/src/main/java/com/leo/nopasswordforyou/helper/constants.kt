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

