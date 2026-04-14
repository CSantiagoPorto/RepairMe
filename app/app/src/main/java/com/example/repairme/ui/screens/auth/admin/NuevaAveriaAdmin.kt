package com.example.repairme.ui.screens.auth.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import com.example.repairme.ui.components.NavItem
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.Naranja
import com.example.repairme.ui.theme.naranjaLetras


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
            if(clienteSeleccionado.id.isEmpty()){
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
            LazyColumn(modifier= Modifier.height(300.dp)) {


                items(clientesFiltrados) {cliente->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        onClick = {clienteSeleccionado=cliente}
                        //Me guardo el cliente pulsado
                    ) {

                            Text("${cliente.name} ${cliente.apellidos}", fontWeight = FontWeight.Bold)
                            Text(cliente.email)

                    }

                }

            }
                //Si mostrar el formulario se pone a true necesito que me muestre los campos
                if (mostrarNuevoFormulario) {
                    OutlinedTextField(
                        value = nuevoNombre,
                        onValueChange = { nuevoNombre = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(
                        value = nuevoApellidos,
                        onValueChange = { nuevoApellidos = it },
                        label = { Text("Apellidos") },
                        modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(
                        value = nuevoEmail,
                        onValueChange = { nuevoEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth())
                    Button(onClick = {repoAdmin.crearUsuarioAdmin(
                        email = nuevoEmail,
                        nombre = nuevoNombre,
                        apellidos = nuevoApellidos,
                        telefono = "",
                        direccion = "",
                        codigoPostal = "",
                        localidad = "",
                        dni = "",
                        exito = { userId ->
                            clienteSeleccionado = Usuario(
                                id = userId,
                                name = nuevoNombre,
                                apellidos = nuevoApellidos,
                                email = nuevoEmail

                            )

                        },
                        error = { msg -> error = msg })}) {
                        Text("Confirmar cliente")
                    }
                }



                //Cuando el cliente no existe nos enseña el botón de crear cliente
               if(clientesFiltrados.isEmpty() && busqueda.isNotEmpty()&&!mostrarNuevoFormulario){
                   Button(onClick = {
                       mostrarNuevoFormulario=true

                   }) {
                           Text("Crear nuevo cliente")
                       }

               }
        }else{
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
                    if(listaEquiposCliente.isNotEmpty()&&equipoSeleccionado==null&& !mostrarNuevoFormulario){
                        Text("Equipos del cliente")
                        listaEquiposCliente.forEach { equipo ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                                onClick = { equipoSeleccionado = equipo }
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        "${equipo.deviceBrand} ${equipo.deviceModel}",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("S/N: ${equipo.deviceSN}")

                                }
                            }
                        }
                        Button(
                            onClick = { mostrarFormularioNuevoEquipo = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Añadir equipo nuevo")
                        }
                    }

                    if (equipoSeleccionado != null) {
                        Text("Equipo seleccionado:", fontWeight = FontWeight.Bold)
                        Text("${equipoSeleccionado!!.deviceBrand} ${equipoSeleccionado!!.deviceModel}", color = Naranja)
                        TextButton(onClick = { equipoSeleccionado = null }) {
                            Text("Cambiar equipo", color = Naranja)
                        }
                    }

                    Text(
                        text = "Crear nueva avería",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Naranja,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )


                   if(equipoSeleccionado==null && mostrarFormularioNuevoEquipo||listaEquiposCliente.isEmpty()){

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
                   }
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
                        verticalAlignment = Alignment.CenterVertically,
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
                            Button(onClick = {
                                prioridadSeleccionada= PrioridadAveria.Baja },
                                colors= ButtonDefaults.buttonColors(
                                    containerColor = if(prioridadSeleccionada == PrioridadAveria.Baja){
                                        Naranja
                                    }else{Color.Gray}
                                )
                                ) { Text("Baja")}
                            Button(onClick = {
                                prioridadSeleccionada= PrioridadAveria.Media },
                                colors= ButtonDefaults.buttonColors(
                                    containerColor = if(prioridadSeleccionada == PrioridadAveria.Media){
                                        Naranja
                                    }else{Color.Gray}
                                )
                                ) { Text("Media")}
                            Button(onClick = {
                                prioridadSeleccionada= PrioridadAveria.Alta },
                                colors= ButtonDefaults.buttonColors(
                                    containerColor = if(prioridadSeleccionada == PrioridadAveria.Alta){
                                        Naranja
                                    }else{Color.Gray}
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
                    Button(
                        onClick = {
                            if(clienteSeleccionado.id.isEmpty()){
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
                                        //Necesito guardarlo para que si lo creo pasarlo al de crear equipos
                                        userId-> clienteSeleccionado= Usuario(
                                        id = userId,
                                            name=nuevoNombre,
                                            apellidos = nuevoApellidos,
                                            email = nuevoEmail

                                    )

                                        if(validarCampos()){


                                        // Crear objeto Equipo con los datos introducidos
                                        val equipo = Equipo(
                                            deviceBrand = marca.trim(),
                                            deviceModel = modelo.trim(),
                                            deviceSN = numeroSerie.trim(),
                                            //averias = listaAverias
                                        )

                                        repoDispositivos.crearEquipoAdmin(
                                            equipo=equipo,
                                            userId = userId,//Este no existía así que me viene del callback de crearUsuarioAdmin
                                            exito = {
                                                    equipoId->
                                                if(añadirAveria){
                                                    repoReparaciones.crearAveriaAdmin(
                                                        averia = Averia(
                                                            tituloAveria=tituloAveria,
                                                            descripcion = descripcionAveria,
                                                            equipoId = equipoId,
                                                            equipoNombre = "${marca} ${modelo}",
                                                            prioridad = prioridadSeleccionada.name

                                                            ), userId = userId,
                                                        exito={onVerAverias()},//Lo cambio para que navegue a la avería por si quiere asignar ya
                                                        fallo = {msg->error=msg}
                                                    )
                                                }else{
                                                    ok=true
                                                }
                                            },
                                            error = {msg->error=msg}

                                        )

                                    }},
                                    error = {msg-> error=msg}


                                )
                            } else {
                                if (equipoSeleccionado != null) {
                                    if (añadirAveria && tituloAveria.isNotEmpty()) {
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
                                        onVerAverias()
                                    }
                                } else if (validarCampos()) {
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
                        colors= ButtonDefaults.buttonColors(
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