package com.example.crudprducts1.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crudprducts1.Product
import com.example.crudprducts1.data.repository.ProductRepository
import com.example.crudprducts1.util.ImagesState
import com.example.crudprducts1.util.ProductState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _product = MutableStateFlow(Product())
    val product: StateFlow<Product> get() = _product

    private val _imagesList = MutableStateFlow<List<ImagesState>>(emptyList())
    val imagesList: StateFlow<List<ImagesState>> get() = _imagesList

    private val _result = MutableStateFlow<ProductState<String>>(ProductState.Idle)
    val result: StateFlow<ProductState<String>> get() = _result

    private val _listProducts = MutableStateFlow<ProductState<List<Product>>>(ProductState.Idle)
    val listProduct: StateFlow<ProductState<List<Product>>> get() = _listProducts

    val isEdit = MutableStateFlow(false)

    init {
        getAllProduct()
    }

    fun setProductForEdit(product: Product) {
        _product.value = product
        isEdit.value = true
        val list = mutableListOf<ImagesState>()
        product.images.forEach { url ->
            list.add(ImagesState.ImageRemote(url))
        }
        _imagesList.value = list
    }

    fun setList(listUri: List<Uri>, context: Context) {
        val list = _imagesList.value.toMutableList()
        listUri.forEach { uri ->
            val byteArray = uriToByteArray(uri, context)
            byteArray?.let {
                list.add(ImagesState.ImageLocal(it))
            }
        }
        _imagesList.value = list
    }

    fun deleteImageLocalAnRemote(position: Int) {
        val currentImages = _imagesList.value.toMutableList()
        if (position in currentImages.indices) {
            val image = currentImages[position]
            if (image is ImagesState.ImageRemote) {
                viewModelScope.launch {
                    repository.deleteImageSupabase(
                        imageName = image.url.substringAfterLast("/"),
                        productId = _product.value.id,
                        position = position
                    )
                }
            }
            currentImages.removeAt(position)
            _imagesList.value = currentImages
        }
    }

    private fun uriToByteArray(uri: Uri, context: Context): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun setNameProduct(name: String) {
        _product.update { it.copy(name = name) }
    }

    fun setPrecio(precio: String) {
        _product.update { it.copy(precio = precio) }
    }

    fun setFecha(fecha: String) {
        _product.update { it.copy(fecha = fecha) }
    }

    fun setHora(hora: String) {
        _product.update { it.copy(hora = hora) }
    }

    fun setId(id: String) {
        _product.update { it.copy(id = id) }
    }

    fun saveProduct() {
        viewModelScope.launch {
            repository.saveProduct(
                product = _product.value,
                currentImage = _imagesList.value
            ) { res ->
                _result.value = res
            }
        }
    }

    fun updateProduct() {
        viewModelScope.launch {
            repository.updateProduct(
                product = _product.value,
                currentImage = _imagesList.value
            ) { res ->
                _result.value = res
            }
        }
    }

    fun getAllProduct() {
        viewModelScope.launch {
            repository.getAllProduct { res ->
                _listProducts.value = res
            }
        }
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            repository.deleteProduct(id)
        }
    }

    fun reset() {
        _product.value = Product()
        _result.value = ProductState.Idle
        _imagesList.value = emptyList()
        isEdit.value = false
    }
}