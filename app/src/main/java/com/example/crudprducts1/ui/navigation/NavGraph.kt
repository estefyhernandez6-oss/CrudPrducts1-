package com.example.crudprducts1.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.crudprducts1.ui.screens.AddProductScreen
import com.example.crudprducts1.ui.screens.AuthScreen
// Asegúrate de importar el Navigate correcto si está en otro archivo, por ejemplo:
// import com.example.crudprducts1.ui.screens.Navigate
import com.example.crudprducts1.ui.screens.HomeScreen
import com.example.crudprducts1.ui.screens.RegisterScreen
import com.example.crudprducts1.ui.viewmodel.AuthViewModel
import com.example.crudprducts1.ui.viewmodel.ProductViewModel

@Composable
fun NavGraph(
    productViewModel: ProductViewModel
) {

    val navController: NavHostController =
        rememberNavController()

    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "auth"
    ) {

        // ==========================================
        // AUTENTICACIÓN
        // ==========================================

        composable("auth") {

            AuthScreen(
                authViewModel = authViewModel
            ) { navAction ->

                // Si tu AuthScreen usa un enum, asegúrate de que coincida con este when.
                // Coloca el cursor sobre 'navAction' o presiona Ctrl+P para ver qué tipo espera.
                when (navAction.toString()) { // Convertirlo a string temporalmente evita el error de incompatibilidad de enums si vienen de archivos distintos

                    "REGISTER" -> {
                        navController.navigate("register")
                    }

                    "HOME" -> {
                        navController.navigate("home") {
                            popUpTo("auth") {
                                inclusive = true
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // REGISTRO
        // ==========================================

        composable("register") {

            RegisterScreen(
                authViewModel = authViewModel,

                onNavigateHome = {

                    navController.navigate("home") {

                        popUpTo("auth") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // ==========================================
        // HOME
        // ==========================================

        composable("home") {

            HomeScreen(
                productViewModel = productViewModel,

                navigate = {
                    navController.navigate("add")
                }
            )
        }

        // ==========================================
        // AGREGAR / EDITAR PRODUCTO
        // ==========================================

        composable("add") {

            AddProductScreen(
                productViewModel = productViewModel,

                navigate = {
                    navController.popBackStack()
                }
            )
        }
    }
}