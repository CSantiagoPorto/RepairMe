package com.example.repairme.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.repairme.ui.screens.AddEquipoScreen
import com.example.repairme.ui.screens.RegisterScreen
import com.example.repairme.ui.screens.auth.LoginScreen

class AppNavigation {

    @Composable
    fun navegarApp(){

        val navController= rememberNavController()//Esto permite que la pantalla persista aunque se cambie la orientación

        NavHost(
            navController= navController,
            startDestination = Rutas.LOGIN.ruta //Le decimos que empiece en el login
        ) {
            composable(Rutas.LOGIN.ruta){
                LoginScreen(
                    onNavigateToRegistro={navController.navigate(Rutas.REGISTRO.ruta)},
                    onNavigateToUserScreen={navController.navigate(Rutas.ADD_EQUIPO.ruta)}

                )

            }
            composable(Rutas.REGISTRO.ruta) {
                RegisterScreen(

                   onNavigateBack ={navController.popBackStack()},
                    onRegisterSucess={navController.popBackStack()}
                    //AQUÍ EN ALGÚN MOMENTO HAY QUE IMPLEMENTAR LA VUELTA AL LOGIN
                )
            }
            composable(Rutas.ADD_EQUIPO.ruta) {
                AddEquipoScreen()
            }
        }
    }

}