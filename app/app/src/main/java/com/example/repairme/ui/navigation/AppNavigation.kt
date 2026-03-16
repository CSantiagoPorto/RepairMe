package com.example.repairme.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.repairme.data.repository.DeviceRepository
import com.example.repairme.data.repository.RepairRepository
import com.example.repairme.ui.screens.AddEquipoScreen
import com.example.repairme.ui.screens.RegisterScreen
import com.example.repairme.ui.screens.RegisterTecnicoScreen
import com.example.repairme.ui.screens.TestCrudScreen
import com.example.repairme.ui.screens.UserScreen
import com.example.repairme.ui.screens.auth.TecnicoScreen
import com.example.repairme.ui.screens.auth.LoginScreen
import com.example.repairme.ui.screens.AdminScreen
import com.example.repairme.ui.screens.DetalleAveriaTecnicoScreen
import com.example.repairme.ui.screens.RegisterTecnicoScreen
import com.example.repairme.ui.screens.RepairsScreen
import com.example.repairme.ui.screens.ProfileScreen
import com.example.repairme.ui.screens.ServicesScreen

class AppNavigation {

    @Composable
    fun navegarApp(){

        val navController = rememberNavController()//Esto permite que la pantalla persista aunque se cambie la orientación

        NavHost(
            navController = navController,
            startDestination = Rutas.LOGIN.ruta //Le decimos que empiece en el login
        ) {
            composable(Rutas.LOGIN.ruta){
                LoginScreen(
                    onNavigateToRegistro = { navController.navigate(Rutas.REGISTRO.ruta) },
                    onNavigateToUserScreen = { navController.navigate(Rutas.USERSCREEN.ruta) },
                    onNavigateToTecnicoScreen = { navController.navigate(Rutas.TECNICOSCREEN.ruta) },
                    onNavigateToAdminScreen = { navController.navigate(Rutas.ADMINSCREEN.ruta) }



                    //Esta es la función real que le pasa el destino. Cuando se llama a la función
                    //sobreescribe la función vacía y ejecuta el navController
                    //Es aquí cuando se decide a dónde ir (
                )
            }

            composable(Rutas.REGISTRO.ruta) {
                RegisterScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onRegisterSucess = { navController.popBackStack() }
                )
            }

            composable(Rutas.USERSCREEN.ruta) {
                UserScreen(
                    onAddEquipo = { navController.navigate(Rutas.ADD_EQUIPO.ruta) },
                    onGoToTestCrud = { navController.navigate(Rutas.TESTCRUD.ruta) },
                    onIrPerfil = { navController.navigate(Rutas.PROFILE.ruta) },
                    onIrServicios = { navController.navigate(Rutas.SERVICES.ruta) }
                )
            }

            composable(Rutas.TECNICOSCREEN.ruta){
                TecnicoScreen(
                    onAddEquipo = { navController.navigate(Rutas.ADD_EQUIPO.ruta) },
                    onAveriaClick = { averiaId -> navController.navigate("detalleAveriaTecnico/$averiaId") },
                    onIrPerfil = { navController.navigate(Rutas.PROFILE.ruta) }
                )
            }

            composable(Rutas.ADMINSCREEN.ruta){
                AdminScreen(
                    onLogOut = { navController.navigate(Rutas.LOGIN.ruta) },
                    onVerAverias = { navController.navigate(Rutas.REPAIRSSCREEN.ruta) },
                    onVerTecnicos = { navController.navigate(Rutas.REGISTRO_TECNICO.ruta) },
                    onIrPerfil = { navController.navigate(Rutas.PROFILE.ruta) }
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
            composable (Rutas.REPAIRSSCREEN.ruta){

                RepairsScreen(
                    onVolver = {navController.popBackStack()}
                )

            }

            composable(Rutas.REGISTRO_TECNICO.ruta){
                RegisterTecnicoScreen(
                    onNavigateBack ={navController.popBackStack()},
                    onRegisterSucess={navController.popBackStack()}
                )
            }

            composable (Rutas.DETALLE_AVERIA_TECNICO.ruta,
                listOf(navArgument("averiaId"){type= NavType.StringType})){
               backStackEntry->
                val averiaId=backStackEntry.arguments?.getString("averiaId")?:""
                DetalleAveriaTecnicoScreen(
                    averiaId = averiaId,
                    onVolver = { navController.popBackStack() }
                )
            }

            composable(Rutas.PROFILE.ruta) {
                ProfileScreen(
                    onVolver = { navController.popBackStack() }
                )
            }

            composable(Rutas.SERVICES.ruta){
                ServicesScreen(
                    onVolver = { navController.popBackStack() }
                )
            }

        }
    }
}