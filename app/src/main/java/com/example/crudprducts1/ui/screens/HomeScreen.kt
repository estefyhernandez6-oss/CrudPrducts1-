package com.example.crudprducts1.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.crudprducts1.ui.componentes.ButtonNavigate
import com.example.crudprducts1.ui.componentes.CardProduct
import com.example.crudprducts1.ui.componentes.DeleateDialog
import com.example.crudprducts1.ui.viewmodel.ProductViewModel
import com.example.crudprducts1.util.ProductState

@Composable
fun HomeScreen(
    productViewModel: ProductViewModel,
    navigate: () -> Unit
) {

    // =====================================================
    // ESTADOS
    // =====================================================

    var showDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var idProduct by rememberSaveable {
        mutableStateOf("")
    }

    val listProductState by
    productViewModel.listProduct.collectAsState()


    // =====================================================
    // DIÁLOGO ELIMINAR
    // =====================================================

    DeleateDialog(
        showDialog = showDialog,

        aceptar = {
            productViewModel.deleteProduct(idProduct)

            showDialog = false
            idProduct = ""
        },

        cancelar = {
            showDialog = false
            idProduct = ""
        }
    )


    // =====================================================
    // CONTENIDO PRINCIPAL
    // =====================================================

    Scaffold(

        floatingActionButton = {

            ButtonNavigate(
                navigate = navigate
            )
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            when (val result = listProductState) {

                // =================================================
                // CARGANDO
                // =================================================

                is ProductState.Loading -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        CircularProgressIndicator()
                    }
                }


                // =================================================
                // PRODUCTOS CARGADOS
                // =================================================

                is ProductState.Success -> {

                    if (result.data.isEmpty()) {

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),

                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "No hay productos registrados",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                    } else {

                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {

                            items(
                                items = result.data,
                                key = { product ->
                                    product.id
                                }
                            ) { product ->

                                CardProduct(

                                    product = product,

                                    // -----------------------------
                                    // ELIMINAR
                                    // -----------------------------

                                    delete = {

                                        idProduct = product.id

                                        showDialog = true
                                    },

                                    // -----------------------------
                                    // EDITAR
                                    // -----------------------------

                                    edit = {

                                        productViewModel
                                            .setProductForEdit(product)

                                        navigate()
                                    }
                                )
                            }
                        }
                    }
                }


                // =================================================
                // ERROR
                // =================================================

                is ProductState.Error -> {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),

                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = result.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }


                // =================================================
                // IDLE
                // =================================================

                ProductState.Idle -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "No hay información disponible",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}