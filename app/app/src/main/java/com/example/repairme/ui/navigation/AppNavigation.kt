package com.example.repairme.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.repairme.data.repository.DeviceRepository
import com.example.repairme.data.repository.NotificationRepository
import com.example.repairme.ui.screens.AddEquipoScreen
import com.example.repairme.ui.screens.RegisterScreen
import com.example.repairme.ui.screens.RegisterTecnicoScreen
import com.example.repairme.ui.screens.TestCrudScreen
import com.example.repairme.ui.screens.UserScreen
import com.example.repairme.ui.screens.TecnicoScreen
import com.example.repairme.ui.screens.AdminScreen
import com.example.repairme.ui.screens.DetalleAveriaTecnicoScreen
import com.example.repairme.ui.screens.DetalleReparacionesFinalizadas
import com.example.repairme.ui.screens.RepairsScreen
import com.example.repairme.ui.screens.ProfileScreen
import com.example.repairme.ui.screens.ServicesScreen
import com.example.repairme.ui.screens.AdminServicesScreen
import com.example.repairme.ui.screens.ClientesPantallaAdminScreen
import com.example.repairme.ui.screens.LoginScreen
import com.example.repairme.ui.screens.PresupuestoDetalleScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.repairme.ui.screens.PresupuestoQueVeElAdmin

import com.example.repairme.ui.screens.auth.admin.VerListasRecoger
import com.example.repairme.ui.screens.ListaTecnicosScreen
import com.example.repairme.ui.screens.NotificationsScreen
import com.example.repairme.ui.screens.auth.admin.NuevaAveriaAdmin
import com.example.repairme.ui.screens.DetalleAveriaComunScreen


class AppNavigation {

    @Composable
    fun navegarApp(){

        val navController = rememberNavController()//Esto permite que la pantalla persista aunque se cambie la orientación

        // SISTEMA GLOBAL DE NOTIFICACIONES
        // Estado compartido que mantiene el contador de notificaciones no leídas
        // Se usa en TODAS las pantallas del mismo rol para que el badge se mantenga sincronizado
        val notificacionesNoLeidas = remember { mutableStateOf(0) }

        // Repositorio de notificaciones para escuchar cambios en Firebase
        val notificationRepo = remember { NotificationRepository() }

        // Escucha los cambios de notificaciones en tiempo real desde Firebase
        // Cuando llega una notificación nueva, actualiza el estado global
        LaunchedEffect(Unit) {
            notificationRepo.escucharNotificacionesNoLeidas { count ->
                // Actualiza el contador de notificaciones para TODAS las pantallas
                notificacionesNoLeidas.value = count
            }
        }

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
                    onVerAverias = { averia ->
                        navController.navigate("detalleAveriaComun/${averia.id}/false/user/Cliente")
                    },
                    onIrHome = { navController.navigate(Rutas.USERSCREEN.ruta) },
                    onIrPerfil = { navController.navigate(Rutas.PROFILE.ruta) },
                    onIrServicios = { navController.navigate(Rutas.SERVICES.ruta) },
                    onIrNotificaciones = { navController.navigate("notifications") },
                    onVerPresupuestos = {averia->navController.navigate("detallePresuCliente/${averia.id}")},
                    onLogOut = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Rutas.LOGIN.ruta) {
                            popUpTo(0)
                        }
                    },
                    // Pasar el contador de notificaciones compartido global
                    // Esto se actualiza automáticamente cuando llegan nuevas notificaciones
                    notificacionesNoLeidas = notificacionesNoLeidas.value
                )
            }

            composable(Rutas.TECNICOSCREEN.ruta){
                TecnicoScreen(
                    onAddEquipo = { navController.navigate(Rutas.ADD_EQUIPO.ruta) },
                    onAveriaClick = { averiaId -> navController.navigate("detalleAveriaTecnico/$averiaId") },
                    onIrHome = { navController.navigate(Rutas.TECNICOSCREEN.ruta) },
                    onIrPerfil = { navController.navigate(Rutas.PROFILE.ruta) },
                    onGestionServicios = { navController.navigate(Rutas.SERVICES.ruta) },
                    onVerDetalleAveria = { averiaId ->
                        navController.navigate("detalleAveriaComun/$averiaId/true/tecnico/Técnico")
                    },
                    onIrNotificaciones = { navController.navigate("notifications") },
                    onReparacionesFinalizadasClick = {averiaID-> navController.navigate("detalleReparacionFinalizada/$averiaID")},
                    onLogOut = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Rutas.LOGIN.ruta) {
                            popUpTo(0)
                        }
                    },
                    // Pasar el contador de notificaciones compartido global
                    notificacionesNoLeidas = notificacionesNoLeidas.value

                )
            }

            composable(Rutas.ADMINSCREEN.ruta){
                AdminScreen(
                    onIrNotificaciones = { navController.navigate("notifications") },
                    onLogOut = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Rutas.LOGIN.ruta) {
                            popUpTo(0)
                        }
                    },
                    onVerAverias = { navController.navigate(Rutas.REPAIRSSCREEN.ruta) },
                    onIrHome = { navController.navigate(Rutas.ADMINSCREEN.ruta) },
                    onVerTecnicos = { navController.navigate(Rutas.LISTA_TECNICOS.ruta) },
                    onIrPerfil = { navController.navigate(Rutas.PROFILE.ruta) },
                    onGestionServicios = { navController.navigate(Rutas.SERVICES_ADMIN.ruta) },
                    onVerClientes = {navController.navigate(Rutas.CLIENTES_ADMIN.ruta)},
                    onVerPresupuestos = { navController.navigate(Rutas.PRESUPUESTOS_ADMIN.ruta) },
                    onVerListaRecoger = { navController.navigate(Rutas.LISTA_PARA_RECOGER_ADMIN.ruta) },
                    onNuevaAveria ={navController.navigate(Rutas.CREAR_AVERIA_ADMIN.ruta)},
                    // Pasar el contador de notificaciones compartido global
                    notificacionesNoLeidas = notificacionesNoLeidas.value
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
                    onVolver = { navController.popBackStack() },
                    onVerAveria = { averia ->
                        navController.navigate("detalleAveriaComun/${averia.id}/true/admin/Admin")
                    },
                    onVerTecnicos = { navController.navigate(Rutas.LISTA_TECNICOS.ruta) },
                    onIrHome = { navController.navigate(Rutas.ADMINSCREEN.ruta) },
                    onIrPerfil = { navController.navigate(Rutas.PROFILE.ruta) },
                    onGestionServicios = { navController.navigate(Rutas.SERVICES_ADMIN.ruta) },
                    onVerClientes = { navController.navigate(Rutas.CLIENTES_ADMIN.ruta) },
                    onVerPresupuestos = { navController.navigate(Rutas.PRESUPUESTOS_ADMIN.ruta) },
                    onIrNotificaciones = { navController.navigate("notifications") },
                    onLogOut = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Rutas.LOGIN.ruta) { popUpTo(0) }
                    },
                    // Pasar el contador de notificaciones compartido global
                    notificacionesNoLeidas = notificacionesNoLeidas.value

                )

            }

            composable(Rutas.REGISTRO_TECNICO.ruta){
                RegisterTecnicoScreen(
                    onNavigateBack ={navController.popBackStack()},
                    onRegisterSucess={navController.popBackStack()}
                )
            }

            composable(Rutas.LISTA_TECNICOS.ruta){
                ListaTecnicosScreen(
                    onVolver = { navController.popBackStack() },
                    onRegistrarTecnico = { navController.navigate(Rutas.REGISTRO_TECNICO.ruta) },
                    onVerAverias = { navController.navigate(Rutas.REPAIRSSCREEN.ruta) },
                    onVerTecnicos = { navController.navigate(Rutas.LISTA_TECNICOS.ruta) },
                    onIrHome = { navController.navigate(Rutas.ADMINSCREEN.ruta) },
                    onIrPerfil = { navController.navigate(Rutas.PROFILE.ruta) },
                    onGestionServicios = { navController.navigate(Rutas.SERVICES_ADMIN.ruta) },
                    onVerClientes = { navController.navigate(Rutas.CLIENTES_ADMIN.ruta) },
                    onVerPresupuestos = { navController.navigate(Rutas.PRESUPUESTOS_ADMIN.ruta) },
                    onIrNotificaciones = { navController.navigate("notifications") },
                    onLogOut = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Rutas.LOGIN.ruta) { popUpTo(0) }
                    },
                    // Pasar el contador de notificaciones compartido global
                    notificacionesNoLeidas = notificacionesNoLeidas.value
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
            composable(
                Rutas.DETALLE_AVERIA_COMUN.ruta,
                listOf(
                    navArgument("averiaId") { type = NavType.StringType },
                    navArgument("puedeEscribir") { type = NavType.BoolType },
                    navArgument("autorRol") { type = NavType.StringType },
                    navArgument("autorNombre") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val averiaId = backStackEntry.arguments?.getString("averiaId") ?: ""
                val puedeEscribir = backStackEntry.arguments?.getBoolean("puedeEscribir") ?: false
                val autorRol = backStackEntry.arguments?.getString("autorRol") ?: ""
                val autorNombre = backStackEntry.arguments?.getString("autorNombre") ?: ""

                DetalleAveriaComunScreen(
                    averiaId = averiaId,
                    puedeEscribirUpdate = puedeEscribir,
                    autorRol = autorRol,
                    autorNombre = autorNombre,
                    onVolver = { navController.popBackStack() }
                )
            }
            composable(Rutas.DETALLE_AVERIA_FINALIZADA.ruta,
                listOf(navArgument("averiaId"){type= NavType.StringType})){
                backStackEntry->
                val averiaId=backStackEntry.arguments?.getString("averiaId")?:""
                DetalleReparacionesFinalizadas(
                    averiaId=averiaId,
                    onVolver = {navController.popBackStack()}
                )

            }

            composable(Rutas.PROFILE.ruta) {
                ProfileScreen(
                    onVolver = { navController.popBackStack() },
                    onLogOut = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Rutas.LOGIN.ruta) { popUpTo(0) }
                    }

                )
            }

            composable(Rutas.SERVICES.ruta){
                ServicesScreen(
                    onVolver = { navController.popBackStack() }
                )
            }

            composable(Rutas.SERVICES_ADMIN.ruta){
                AdminServicesScreen(
                    onVolver = { navController.popBackStack() }
                )
            }

            // Pantalla de pruebas CRUD
            composable(Rutas.TESTCRUD.ruta) {
                TestCrudScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Rutas.CLIENTES_ADMIN.ruta) {
                ClientesPantallaAdminScreen(
                    onIrHome = { navController.navigate(Rutas.ADMINSCREEN.ruta) },
                    onVolver = { navController.popBackStack() },
                    onIrPerfil = { navController.navigate(Rutas.PROFILE.ruta) },
                    onGestionServicios = { navController.navigate(Rutas.SERVICES_ADMIN.ruta) },
                    onIrNotificaciones = { navController.navigate("notifications") },
                    onLogOut = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Rutas.LOGIN.ruta) { popUpTo(0) }
                    },
                    // Pasar el contador de notificaciones compartido global
                    notificacionesNoLeidas = notificacionesNoLeidas.value
                )
            }
            composable(Rutas.DETALLE_PRESUPUESTO.ruta,
                listOf(navArgument("averiaId"){type= NavType.StringType})){
                    backStackEntry->
                val averiaId=backStackEntry.arguments?.getString("averiaId")?:""
                PresupuestoDetalleScreen(
                    averiaId=averiaId,
                    onVolver = {navController.popBackStack()}
                )

            }
            composable(Rutas.PRESUPUESTOS_ADMIN.ruta) {
                PresupuestoQueVeElAdmin(
                    onIrHome = { navController.navigate(Rutas.ADMINSCREEN.ruta) },
                    onVolver = { navController.popBackStack() },
                    onIrPerfil = { navController.navigate(Rutas.PROFILE.ruta) },
                    onGestionServicios = { navController.navigate(Rutas.SERVICES_ADMIN.ruta) },
                    onIrNotificaciones = { navController.navigate("notifications") },
                    onLogOut = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Rutas.LOGIN.ruta) { popUpTo(0) }
                    },
                    // Pasar el contador de notificaciones compartido global
                    notificacionesNoLeidas = notificacionesNoLeidas.value
                )
            }

            composable(Rutas.LISTA_PARA_RECOGER_ADMIN.ruta) {
                VerListasRecoger(
                    onIrHome = { navController.navigate(Rutas.ADMINSCREEN.ruta) },
                    onVolver = { navController.popBackStack() },
                    onIrPerfil = { navController.navigate(Rutas.PROFILE.ruta) },
                    onGestionServicios = { navController.navigate(Rutas.SERVICES_ADMIN.ruta) },
                    onIrNotificaciones = { navController.navigate("notifications") },
                    onLogOut = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Rutas.LOGIN.ruta) { popUpTo(0) }
                    },
                    // Pasar el contador de notificaciones compartido global
                    notificacionesNoLeidas = notificacionesNoLeidas.value
                )
            }




            // Esta pantalla es común para todos los roles (User, Tecnico, Admin)
            // Determina el rol del usuario para navegar a la pantalla principal correcta cuando presione HOME
            composable("notifications") {
                // Obtenemos el UID del usuario actual autenticado en Firebase
                val auth = FirebaseAuth.getInstance()
                val uid = auth.currentUser?.uid

                // Accedemos a la base de datos de Firebase para obtener datos del usuario
                val db = FirebaseDatabase.getInstance("https://repairme-956fd-default-rtdb.europe-west1.firebasedatabase.app")

                // Estado local para almacenar el rol del usuario
                // Se inicializa como "user" por defecto en caso de error
                val rolUsuario = remember { mutableStateOf("user") }

                // LaunchedEffect para obtener el rol del usuario de forma asincrónica
                // Se ejecuta cuando el UID del usuario cambia
                LaunchedEffect(uid) {
                    if (uid != null) {
                        // Consultamos la base de datos para obtener el rol del usuario
                        db.getReference("users").child(uid).get()
                            .addOnSuccessListener { snapshot ->
                                // Extraemos el valor de "role" del documento del usuario
                                val role = snapshot.child("role").value as? String ?: "user"
                                // Actualizamos el estado con el rol obtenido
                                rolUsuario.value = role
                            }
                    }
                }

                // Callback para el botón HOME
                // Navega a la pantalla principal según el rol del usuario
                val onIrHome = {
                    when (rolUsuario.value.lowercase()) {
                        // Si es User, va a UserScreen
                        "user" -> navController.navigate(Rutas.USERSCREEN.ruta) {
                            // Limpia la pila de navegación para que no haya duplicados
                            popUpTo(Rutas.USERSCREEN.ruta) { inclusive = true }
                        }
                        // Si es Técnico, va a TecnicoScreen
                        "tecnico" -> navController.navigate(Rutas.TECNICOSCREEN.ruta) {
                            popUpTo(Rutas.TECNICOSCREEN.ruta) { inclusive = true }
                        }
                        // Si es Admin, va a AdminScreen
                        "admin" -> navController.navigate(Rutas.ADMINSCREEN.ruta) {
                            popUpTo(Rutas.ADMINSCREEN.ruta) { inclusive = true }
                        }
                        // Por defecto, va a UserScreen
                        else -> navController.navigate(Rutas.USERSCREEN.ruta) {
                            popUpTo(Rutas.USERSCREEN.ruta) { inclusive = true }
                        }
                    }
                }

                // Callback para el botón de SERVICIOS
                // Navega a la pantalla de servicios según el rol del usuario
                val onIrServicios = {
                    when (rolUsuario.value.lowercase()) {
                        // Si es Admin, va a AdminServicesScreen
                        "admin" -> navController.navigate(Rutas.SERVICES_ADMIN.ruta)
                        // Para User y Tecnico, va a Services
                        else -> navController.navigate(Rutas.SERVICES.ruta)
                    }
                }

                NotificationsScreen(
                    onIrHome = onIrHome,
                    onIrPerfil = { navController.navigate(Rutas.PROFILE.ruta) },
                    onIrServicios = onIrServicios,
                    onIrNotificaciones = { navController.navigate("notifications") },
                    onLogOut = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Rutas.LOGIN.ruta) { popUpTo(0) }
                    },
                    onVolver = { navController.popBackStack() }
                )
            }

            composable (Rutas.CREAR_AVERIA_ADMIN.ruta){
                NuevaAveriaAdmin (
                    onIrHome = { navController.navigate(Rutas.ADMINSCREEN.ruta) },
                    onVolver = { navController.popBackStack() },
                    onVerAverias = { navController.navigate(Rutas.REPAIRSSCREEN.ruta) },
                    onIrPerfil = { navController.navigate(Rutas.PROFILE.ruta) },
                    onGestionServicios = { navController.navigate(Rutas.SERVICES_ADMIN.ruta) },
                    onLogOut = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Rutas.LOGIN.ruta) { popUpTo(0) }
                    }

                )

            }






        }
    }
}