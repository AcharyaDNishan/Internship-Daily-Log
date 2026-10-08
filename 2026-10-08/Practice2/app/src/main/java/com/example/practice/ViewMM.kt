package com.example.practice

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

class ViewMM {
    var backgroundColor: Color by mutableStateOf(Color.White)
        private set
    fun bchange(){
        backgroundColor = Color.LightGray
    }
}