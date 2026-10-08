package com.example.practice

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class ViewMM : ViewModel() {
    var backgroundColor: Color by mutableStateOf(Color.White)
        private set
    fun bchange(){
        backgroundColor = Color.LightGray
    }
}