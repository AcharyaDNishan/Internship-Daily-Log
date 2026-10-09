package com.example.project

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ImageC: ViewModel() {
    var images by mutableStateOf<List<Image>>(emptyList())
        private set

    fun updateImages(newImages: List<Image>) {
        images = newImages
    }
}
