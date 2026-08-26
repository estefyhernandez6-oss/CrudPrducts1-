package com.example.crudprducts1.util

sealed class ImagesState {
    data class ImageLocal(val byteArray: ByteArray) : ImagesState()
    data class ImageRemote(val url: String) : ImagesState() // ← Debe decir 'url'
}