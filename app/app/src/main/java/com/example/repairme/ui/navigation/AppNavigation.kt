package com.example.repairme.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.repairme.data.repository.DeviceRepository
import com.example.repairme.ui.screens.AddEquipoScreen
import com.example.repairme.ui.screens.RegisterScreen
import com.example.repairme.ui.screens.UserScreen
import com.example.repairme.ui.screens.auth.AdminScreen
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
                    onNavigateToUserScreen={navController.navigate(Rutas.USERSCREEN.ruta)},
                    onNavigateToAdminScreen={navController.navigate(Rutas.ADMINSCREEN.ruta)}
                    //Esta es la función real que le pasa el destino. Cuando se llama a la función
                    //sobreescribe la función vacía y ejecuta el navController
                    //Es aquí cuando se decide a dónde ir (

                )

            }
            composable(Rutas.REGISTRO.ruta) {
                RegisterScreen(

                   onNavigateBack ={navController.popBackStack()},
                    onRegisterSucess={navController.popBackStack()}

                )
            }
            composable(Rutas.USERSCREEN.ruta) {
                UserScreen(
                    onAddEquipo = {navController.navigate(Rutas.ADD_EQUIPO.ruta)},
                    verEquipos= {navController.navigate(Rutas.ADD_EQUIPO.ruta)}
                )
            }
            composable(Rutas.ADMINSCREEN.ruta){
                AdminScreen(
                    onAddEquipo= {navController.navigate(Rutas.ADD_EQUIPO.ruta)}
                )
            }

            composable(Rutas.ADD_EQUIPO.ruta) {
                val repo = DeviceRepository()
                val context = LocalContext.current
                AddEquipoScreen(
                    onGuardar = { equipo ->
                        repo.crearEquipo(
                            equipo = equipo,
                            exito  = { Toast.makeText(context, "Equipo guardado", Toast.LENGTH_LONG).show() },
                            error  = { causa -> Toast.makeText(context, causa, Toast.LENGTH_LONG).show() }
                        )
                    },
                    onVolver = { navController.popBackStack() }
                )
            }

        }
    }

}