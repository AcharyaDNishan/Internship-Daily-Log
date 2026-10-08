package com.example.practice

import androidx.compose.foundation.Image
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class ViewMM : ViewModel() {
    var backgroundColor: Color by mutableStateOf(Color.White)
        private set
    fun bchange()= runBlocking{
        backgroundColor= Color.Red
    }
}