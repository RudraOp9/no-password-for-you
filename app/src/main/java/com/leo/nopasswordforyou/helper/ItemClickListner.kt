package com.leo.nopasswordforyou.helper

import android.view.View

interface ItemClickListner {
    fun onClick(v: View, id: String, Title: String, Desc: String, alias: String)
}
