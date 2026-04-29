package com.example.repairme.ui.screens.admin

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.repairme.data.repository.AuthRepository
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.Equipo
import com.example.repairme.data.model.EstadoAveria
import com.example.repairme.data.model.PrioridadAveria
import com.example.repairme.data.model.Usuario
import com.example.repairme.data.repository.AdminRepository
import com.example.repairme.data.repository.DeviceRepository
import com.example.repairme.data.repository.RepairRepository
import com.example.repairme.data.repository.UserRepository
import com.example.repairme.ui.components.BaseScreen

// Colores
import com.example.repairme.ui.theme.Naranja
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.botonNaranja
import com.example.repairme.utils.generarFactura

// Para previsualizar la pantalla en design

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTecnicoScreen(
    onNavigateBack: () -> Unit = {},//Con esta función volvemos al login
    onRegisterSucess: () -> Unit = {}
    //Dejo esta función aquí porque la voy a usar para volver al login cuando
    //el registro haya sido exitoso
) {

    //Creo instancia del repo y del context
    val repo = AuthRepository()
    val context = LocalContext.current

    // Variables/inputs del formulario
    var email by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }
    var nombre by rememberSaveable { mutableStateOf("") }

    // Variables para decir si OK o error
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var ok by rememberSaveable { mutableStateOf(false) }

    // Función de validación
    fun validarCampos(): Boolean {
        // Si algún campo está vacío -> error
        if (email.trim().isEmpty() || !email.contains("@")) {
            error = "Email inválido"
            return false
        }

        if (pass.trim().length < 8) {
            error = "Contraseña debe tener al menos 8 caracteres"
            return false
        }
        if (
            nombre.trim().isEmpty()
        ) {
            error = "Rellena todos los campos"
            return false
        }

        // Si todo va bien, limpiamos error
        error = null
        return true
    }

    Scaffold(
        containerColor = GrisFondoPantalla,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Registrar Técnico",
                        color = Naranja
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Naranja
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GrisFondoPantalla
                )
            )
        }
    ) { innerPadding ->
        // UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GrisFondoPantalla)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Titulo de la pantalla
            // Lo dejo comentado porque ahora el título ya está en la TopAppBar
            /*
            Text(
                text = "Registrar Técnico",
                style = MaterialTheme.typography.headlineMedium,
                color = Naranja,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            */

            // Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    error = null
                },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    error = null
                },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                enabled = !ok
            )

            OutlinedTextField(
                value = pass,
                onValueChange = {
                    pass = it
                    error = null
                },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                enabled = !ok
            )

            // Mensajes de error/ok
            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (ok) {
                Text("Registro enviado ✅")
            }

            // Botón de registro
            Button(
                onClick = {
                    if (validarCampos()) {
                        ok = true
                        repo.crearUsuario(
                            email = email.trim(),
                            password = pass.trim(),
                            nombre = nombre.trim(),
                            apellidos = "", // Empty porque Tecnico no tiene apellidos en su model
                            telefono = "", // Empty for tecnico
                            direccion = "", // Empty for tecnico
                            codigoPostal = "", // Empty for tecnico
                            localidad = "", // Empty for tecnico
                            dni = "", // Empty for tecnico
                            role = "tecnico", // Set role to TECNICO para que coincida con el model
                            creadoOK = {
                                ok = false
                                Toast.makeText(context, "Técnico creado", Toast.LENGTH_LONG).show()
                                onRegisterSucess()
                            },
                            creadoError = { mensaje ->
                                ok = false
                                error = mensaje
                                Toast.makeText(context, "Error: $mensaje", Toast.LENGTH_LONG).show()
                            }
                        )
                    } else {
                        ok = false
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Naranja,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar Técnico")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterTecnicoScreenPreview() {
    MaterialTheme {
        RegisterTecnicoScreen()
    }
}

@Composable
fun NuevaAveriaAdmin(
    onIrHome: () -> Unit = {},
    onVolver: () -> Unit = {},
    onVerAverias: () -> Unit = {},
    onIrPerfil: () -> Unit = {},
    onGestionServicios: () -> Unit = {},
    onIrNotificaciones: () -> Unit = {},
    onLogOut: () -> Unit = {}
) {
    val repoReparaciones = remember { RepairRepository() }
    val repoDispositivos = remember { DeviceRepository() }
    val context = LocalContext.current

    val repoAdmin= remember { AdminRepository(context) }
    val repoUsuario=remember { UserRepository() }


    //var todasAverias by remember { mutableStateOf(listOf<Averia>()) }
    var busqueda by remember { mutableStateOf("") }
    var clienteSeleccionado by remember { mutableStateOf(Usuario()) }
    var listaClientes by remember { mutableStateOf(listOf<Usuario>()) }
    var marca by rememberSaveable { mutableStateOf("") }
    var modelo by rememberSaveable { mutableStateOf("") }
    var numeroSerie by remember { mutableStateOf("") }
    var prioridadSeleccionada by remember { mutableStateOf(PrioridadAveria.Media) }


    var añadirAveria by rememberSaveable { mutableStateOf(false) }
    var tituloAveria by rememberSaveable { mutableStateOf("") }
    var descripcionAveria by rememberSaveable { mutableStateOf("") }
    var reparaciones by remember { mutableStateOf(listOf<Averia>()) }

    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var ok by rememberSaveable { mutableStateOf(false) }
    var mostrarNuevoFormulario by remember { mutableStateOf(false) }
    var listaEquiposCliente by remember { mutableStateOf(listOf<Equipo>()) }
    var equipoSeleccionado by remember { mutableStateOf<Equipo?>(null) }
    var mostrarFormularioNuevoEquipo by remember { mutableStateOf(false) }


    var nuevoNombre by remember { mutableStateOf("") }
    var nuevoApellidos by remember { mutableStateOf("") }
    var nuevoEmail by remember { mutableStateOf("") }
    var nuevoTelefono by remember { mutableStateOf("") }
    var nuevoDni by remember { mutableStateOf("") }
    var nuevaDireccion by remember { mutableStateOf("") }
    var nuevoCodigoPostal by remember { mutableStateOf("") }
    var nuevaLocalidad by remember { mutableStateOf("") }
    var rgpdAceptado by remember { mutableStateOf(false) }


    var notificacionesNoLeidas by remember { mutableStateOf(0) }


    LaunchedEffect(Unit) {
        repoUsuario.obtenerUsuariosTodos(
            fallo = { },
            exito = { clientes -> listaClientes = clientes }
        )

    }
    LaunchedEffect(clienteSeleccionado.id) {
        if (clienteSeleccionado.id.isNotEmpty()){
            marca = ""
            modelo = ""
            numeroSerie = ""
            tituloAveria = ""
            descripcionAveria = ""
            añadirAveria = false
            equipoSeleccionado = null
            mostrarFormularioNuevoEquipo = false

            repoDispositivos.obtenerEquiposPorUsuario(
                userId = clienteSeleccionado.id,
                error={},
                exito = {listaEquiposCliente=it}
            )
        }
    }

    val clientesFiltrados = listaClientes.filter {
        it.name.contains(busqueda, ignoreCase = true) ||
                it.apellidos.contains(busqueda, ignoreCase = true) ||
                it.dni.equals(busqueda, ignoreCase = true)||
                it.email.equals(busqueda, ignoreCase = true)
    }
    fun validarCampos(): Boolean {
        if (
            marca.trim().isEmpty() ||
            modelo.trim().isEmpty() ||
            numeroSerie.trim().isEmpty()
        ) {
            error = "Rellena Marca, Modelo y Nº de serie"
            return false
        }

        // Si se activa Añadir avería, debe tener contenido
        if (añadirAveria && descripcionAveria.trim().isEmpty()) {
            error = "Describe la avería o desmarca 'Añadir avería'"
            return false
        }

        error = null
        return true
    }

    BaseScreen(
        title = "Crear Reparación",
        onIrHome = onIrHome,
        onIrPerfil = onIrPerfil,
        onGestionServicios = onGestionServicios,
        onLogOut = onLogOut,
        onVolver = onVolver,
        onNotificationsClick = onIrNotificaciones,
        notificationBadgeCount = notificacionesNoLeidas


    ) { modifier ->
        Column(modifier = modifier.padding(16.dp)) {
            if (clienteSeleccionado.id.isEmpty()) {
                //Si no hay un cliente filtrado, muestro el buscador
                //Va a empezar como objeto vacío
                //Si no pulso tarjeta el id está vacío así queme va a enseñar el buscador
                //Si pulso se sale por el else

                TextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    label = { Text("Buscar por nombre, apellidos, dni o email") },
                    modifier = Modifier.fillMaxWidth()
                )
                LazyColumn(modifier = Modifier.height(300.dp)) {


                    items(clientesFiltrados) { cliente ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            onClick = { clienteSeleccionado = cliente }
                            //Me guardo el cliente pulsado
                        ) {

                            Text(
                                "${cliente.name} ${cliente.apellidos}",
                                fontWeight = FontWeight.Companion.Bold
                            )
                            Text(cliente.email)

                        }

                    }

                }
                //Si mostrar el formulario se pone a true necesito que me muestre los campos
                //Esto pasa al pulsar el botón
                if (mostrarNuevoFormulario) {
                    OutlinedTextField(
                        value = nuevoNombre,
                        onValueChange = { nuevoNombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevoApellidos,
                        onValueChange = { nuevoApellidos = it },
                        label = { Text("Apellidos") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevoEmail,
                        onValueChange = { nuevoEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevoTelefono,
                        onValueChange = { nuevoTelefono = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevoDni,
                        onValueChange = { nuevoDni = it },
                        label = { Text("DNI") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevaDireccion,
                        onValueChange = { nuevaDireccion = it },
                        label = { Text("Dirección") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevoCodigoPostal,
                        onValueChange = { nuevoCodigoPostal = it },
                        label = { Text("Código Postal") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nuevaLocalidad,
                        onValueChange = { nuevaLocalidad = it },
                        label = { Text("Localidad") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = rgpdAceptado,
                            onCheckedChange = {rgpdAceptado=it}
                        )
                        Text("Acepto la política de protección de datos")
                    }
                    Button(onClick = {
                        if(!rgpdAceptado){
                            error="Es obligatorio aceptar la política de protección de datos"
                            return@Button
                        }
                        repoAdmin.crearUsuarioAdmin(
                            email = nuevoEmail,
                            nombre = nuevoNombre,
                            apellidos = nuevoApellidos,
                            telefono = nuevoTelefono,
                            direccion = nuevaDireccion,
                            codigoPostal = nuevoCodigoPostal,
                            localidad = nuevaLocalidad,
                            dni = nuevoDni,
                            exito = { userId ->
                                clienteSeleccionado = Usuario(
                                    id = userId,
                                    name = nuevoNombre,
                                    apellidos = nuevoApellidos,
                                    email = nuevoEmail

                                )

                            },
                            error = { msg -> error = msg })
                    }) {
                        Text("Confirmar cliente")
                    }
                }
                //Cuando el cliente no existe nos enseña el botón de crear cliente
                if (clientesFiltrados.isEmpty() && busqueda.isNotEmpty() && !mostrarNuevoFormulario) {
                    Button(onClick = {
                        mostrarNuevoFormulario = true

                    }) {
                        Text("Crear nuevo cliente")
                    }

                }
            } else {//Por aquí entra si selecciono cliente
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(GrisFondoPantalla)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Título
                    //Necesito que me cree las tarjetas con los equipos
                    if (listaEquiposCliente.isNotEmpty() && equipoSeleccionado == null && !mostrarNuevoFormulario) {
                        //Aquí el cliente tiene equipos pero no he seleccionado ninguno y no le he dado a crear ninguno
                        Text("Equipos del cliente")
                        listaEquiposCliente.forEach { equipo ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                                onClick = {
                                    equipoSeleccionado = equipo
                                }//Aquí me va a guardar en la variable el equipo sobre el que he hecho click. Ahora sólo lo tengo que enseñar
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        "${equipo.deviceBrand} ${equipo.deviceModel}",
                                        fontWeight = FontWeight.Companion.Bold
                                    )
                                    Text("S/N: ${equipo.deviceSN}")

                                }
                            }
                        }
                        Button(
                            onClick = {
                                mostrarFormularioNuevoEquipo = true
                            },//Aquí le cambio el estado al formulario
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Añadir equipo nuevo")
                        }
                    }

                    if (equipoSeleccionado != null) {//Si ya tengo el equipo seleccionado mostramos datos y el botón para quitar la selección
                        Text("Equipo seleccionado:", fontWeight = FontWeight.Companion.Bold)
                        Text(
                            "${equipoSeleccionado!!.deviceBrand} ${equipoSeleccionado!!.deviceModel}",
                            color = Naranja
                        )
                        TextButton(onClick = { equipoSeleccionado = null }) {
                            Text("Cambiar equipo", color = Naranja)
                        }
                    }

                    Text(
                        text = "Crear nueva avería",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Naranja,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Companion.Center
                    )


                    if (equipoSeleccionado == null && mostrarFormularioNuevoEquipo || listaEquiposCliente.isEmpty()) {
                        //Su no hay equipo y se pulsó añadir equipo va a entrar por aquí y mostrar el formulario

                        // Marca
                        OutlinedTextField(
                            value = marca,
                            onValueChange = {
                                marca = it
                                error = null
                                ok = false
                            },
                            label = { Text("Marca") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Modelo
                        OutlinedTextField(
                            value = modelo,
                            onValueChange = {
                                modelo = it
                                error = null
                                ok = false
                            },
                            label = { Text("Modelo") },
                            modifier = Modifier.fillMaxWidth()
                        )


                        // Número de serie (números y letras)
                        OutlinedTextField(
                            value = numeroSerie,
                            onValueChange = {
                                numeroSerie = it
                                error = null
                                ok = false
                            },
                            label = { Text("Número de serie") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }//Aquí acaba el if que comprueba que no haya equipo
                    OutlinedTextField(
                        value = tituloAveria,
                        onValueChange = {
                            tituloAveria = it
                            error = null
                            ok = false
                        },
                        label = { Text("Título de la avería") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Añadir avería opcional (con un switch)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Companion.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Añadir avería (opcional)")
                        Switch(
                            checked = añadirAveria,
                            onCheckedChange = {
                                añadirAveria = it
                                error = null
                                ok = false
                                if (!it) {
                                    descripcionAveria = ""
                                }
                            }
                        )
                    }

                    // Si el switch anterior está activado, aparece el cuadro de texto de descripción
                    if (añadirAveria) {
                        OutlinedTextField(
                            value = descripcionAveria,
                            onValueChange = {
                                descripcionAveria = it
                                error = null
                                ok = false
                            },
                            label = { Text("Describe el problema") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            maxLines = 6
                        )
                        Row() {
                            Button(
                                onClick = {
                                    prioridadSeleccionada = PrioridadAveria.Baja
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (prioridadSeleccionada == PrioridadAveria.Baja) {
                                        Naranja
                                    } else {
                                        Color.Gray
                                    }
                                )
                            ) { Text("Baja") }
                            Button(
                                onClick = {
                                    prioridadSeleccionada = PrioridadAveria.Media
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (prioridadSeleccionada == PrioridadAveria.Media) {
                                        Naranja
                                    } else {
                                        Color.Gray
                                    }
                                )
                            ) { Text("Media") }
                            Button(
                                onClick = {
                                    prioridadSeleccionada = PrioridadAveria.Alta
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (prioridadSeleccionada == PrioridadAveria.Alta) {
                                        Naranja
                                    } else {
                                        Color.Gray
                                    }
                                )
                            ) { Text("Alta") }
                        }
                    }

                    // Mensajes
                    if (error != null) {
                        Text(text = error!!, color = MaterialTheme.colorScheme.error)
                    }

                    if (ok) {
                        Text("Avería guardada ")
                    }

                    // Botón Guardar
                    //Tengo tres posibilidades:
                    //El cliente NO existe
                    //El cliente existe y es reparaciónde equipo previo
                    //El cliente existe y el equipo es NUEVO
                    Button(

                        onClick = {
                            if (clienteSeleccionado.id.isEmpty()) {//Si no existe USUARIO haces esto
                                repoAdmin.crearUsuarioAdmin(
                                    email = nuevoEmail,
                                    nombre = nuevoNombre,
                                    apellidos = nuevoApellidos,
                                    telefono = "",
                                    direccion = "",
                                    codigoPostal = "",
                                    localidad = "",
                                    dni = "",
                                    exito = {
                                        //Necesito guardarlo para que si lo creo pasarlo al de crear equipos y el equipo contenga el userId
                                            userId ->
                                        clienteSeleccionado = Usuario(
                                            id = userId,
                                            name = nuevoNombre,
                                            apellidos = nuevoApellidos,
                                            email = nuevoEmail

                                        )

                                        if (validarCampos()) {//Si el usuario no existe necesariamente tampoco tiene equipos. Los creo


                                            // Crear objeto Equipo con los datos introducidos
                                            val equipo = Equipo(
                                                deviceBrand = marca.trim(),
                                                deviceModel = modelo.trim(),
                                                deviceSN = numeroSerie.trim(),
                                                //averias = listaAverias
                                            )

                                            repoDispositivos.crearEquipoAdmin(
                                                equipo = equipo,
                                                userId = userId,//Este no existía así que me viene del callback de crearUsuarioAdmin
                                                exito = { equipoId ->
                                                    if (añadirAveria) {
                                                        repoReparaciones.crearAveriaAdmin(
                                                            averia = Averia(
                                                                tituloAveria = tituloAveria,
                                                                descripcion = descripcionAveria,
                                                                equipoId = equipoId,
                                                                equipoNombre = "${marca} ${modelo}",
                                                                prioridad = prioridadSeleccionada.name

                                                            ), userId = userId,
                                                            exito = { onVerAverias() },//Lo cambio para que navegue a la avería por si quiere asignar ya
                                                            fallo = { msg -> error = msg }
                                                        )
                                                    } else {
                                                        ok =
                                                            true//No necesito crear la avería. Sólo quiero el mensaje de avería guardada
                                                    }
                                                },
                                                error = { msg -> error = msg }

                                            )

                                        }
                                    },
                                    error = { msg -> error = msg }


                                )
                            } else {//Ek cliente ya existe entonces y el equipo ha sido seleccionado de la lista
                                if (equipoSeleccionado != null) {
                                    if (añadirAveria && tituloAveria.isNotEmpty()) {
                                        //Necesito que el switch esté activado y que el título de la avería puesto porque si no, me sale sin título
                                        repoReparaciones.crearAveriaAdmin(
                                            averia = Averia(
                                                tituloAveria = tituloAveria,
                                                descripcion = descripcionAveria,
                                                equipoId = equipoSeleccionado!!.devicesId,
                                                equipoNombre = "${equipoSeleccionado!!.deviceBrand} ${equipoSeleccionado!!.deviceModel}",
                                                prioridad = prioridadSeleccionada.name
                                            ),
                                            userId = clienteSeleccionado.id,
                                            exito = { onVerAverias() },
                                            fallo = { msg -> error = msg }
                                        )
                                    } else {
                                        onVerAverias()//Quiero que lo redirija para que me asigne un técnico a la reparación
                                    }
                                } else if (validarCampos()) {//Esto salta en el tercer caso. QUe no hay equipo seleccionado
                                    val equipo = Equipo(
                                        deviceBrand = marca.trim(),
                                        deviceModel = modelo.trim(),
                                        deviceSN = numeroSerie.trim(),
                                    )
                                    repoDispositivos.crearEquipoAdmin(
                                        equipo = equipo,
                                        userId = clienteSeleccionado.id,
                                        exito = { equipoId ->
                                            if (añadirAveria) {
                                                repoReparaciones.crearAveriaAdmin(
                                                    averia = Averia(
                                                        tituloAveria = tituloAveria,
                                                        descripcion = descripcionAveria,
                                                        equipoId = equipoId,
                                                        equipoNombre = "${marca} ${modelo}",
                                                        prioridad = prioridadSeleccionada.name
                                                    ),
                                                    userId = clienteSeleccionado.id,
                                                    exito = { onVerAverias() },
                                                    fallo = { msg -> error = msg }
                                                )
                                            } else {
                                                onVerAverias()
                                            }
                                        },
                                        error = { msg -> error = msg }
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Naranja,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar avería")
                    }

                    // Volver
                    TextButton(onClick = onVolver) {
                        Text("Volver", color = Naranja)
                    }
                }
            }
        }
    }
}

@Composable
fun VerListasRecoger(
    onIrHome: () -> Unit = {},
    onVolver: () -> Unit = {},
    onIrPerfil: () -> Unit = {},
    onGestionServicios: () -> Unit = {},
    onIrNotificaciones: () -> Unit = {},
    onLogOut: () -> Unit = {},
    notificacionesNoLeidas: Int = 0
){
    val repo = remember { RepairRepository() }
    var todasAverias by remember { mutableStateOf(listOf<Averia>()) }
    var busqueda by remember { mutableStateOf("") }
    val userRepo = remember { UserRepository() }
    var mapaUsuarios by remember { mutableStateOf(mapOf<String, Usuario>()) }
    val listasParaRecoger= todasAverias.filter {
        it.estado== EstadoAveria.ListaParaRecoger.name}.sortedBy { it.fechaListo }
    //Neceito filtrar y ordenar la lista para que nos enseñe las que están listas

    val hace10dias = System.currentTimeMillis() - (10L * 24 * 60 * 60 * 1000)
    val context = LocalContext.current



    LaunchedEffect(Unit) {

        repo.obtenerAveriasTodas(
            fallo = {},
            exito = { todasAverias = it },


            )
        userRepo.obtenerUsuariosTodos(
            fallo = {},
            exito = { lista -> mapaUsuarios = lista.associateBy { it.id } }
            //Esto nos va a convertir la lista en un mapa de usuarios y luego no hay que recorrer la lista entera todas las veces
        )

    }

    BaseScreen (
        title = "Listas para recoger",
        onIrHome = onIrHome,
        onIrPerfil = onIrPerfil,
        onGestionServicios = onGestionServicios,
        onLogOut = onLogOut,
        onVolver = onVolver,
        onNotificationsClick = onIrNotificaciones,
        notificationBadgeCount = notificacionesNoLeidas

    ) {modifier ->
        // He cambiado la cabecera a azul para enseñaros como iría con el otro enfoque



        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(GrisFondoPantalla)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Listas para recoger",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9CA3AF),
                    letterSpacing = 0.06.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(listasParaRecoger) { averia ->
                val cliente = mapaUsuarios[averia.userId]
                val subtotal = averia.lineasPresupuesto.sumOf { it.cantidad * it.precioUnitario }
                val total = subtotal * 1.21
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(
                        2.dp, if (
                            averia.fechaListo <= hace10dias
                        ) {
                            Color.Red
                        } else {
                            botonNaranja
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = averia.tituloAveria,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        Text(
                            text = "${cliente?.name ?: ""} ${cliente?.apellidos ?: ""}",
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Text(
                            text = "${cliente?.phone ?: ""}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = botonNaranja,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "${cliente?.email ?: ""}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = botonNaranja,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Button(
                            onClick = {
                                val archivo= generarFactura(
                                    context = context,
                                    averia = averia,
                                    cliente = cliente!!
                                )
                                val uri= FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.provider",
                                    archivo
                                )

                                val intent= Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "application/pdf")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(intent)
                            }

                        ) {
                            Text("Generar factura")
                        }
                    }
                }
            }




        }
    }

}