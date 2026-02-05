package com.example.repairme.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.repairme.ui.screens.auth.LoginScreen

class AppNavigation {

    @Composable
    fun navegarApp(){

        val navController= rememberNavController()

        NavHost(
            navController= navController,
            startDestination = Rutas.LOGIN.ruta //Le decimos que empiece en el login
        ) {
            composable(Rutas.LOGIN.ruta){
                LoginScreen(
                    onNavigateToRegistro={navController.navigate(Rutas.REGISTRO.ruta)}

                )

            }
            composable(Rutas.USERSCREEN.ruta) {
                LoginScreen(//Cambia esto a UserScreen cuando te la pasen
                    onNavigateToUserScreen={navController.navigate(Rutas.USERSCREEN.ruta)}
                )
            }
        }
    }

}