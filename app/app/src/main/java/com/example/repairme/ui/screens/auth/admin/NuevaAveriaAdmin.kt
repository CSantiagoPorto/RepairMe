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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.Equipo
import com.example.repairme.data.model.EstadoAveria
import com.example.repairme.data.model.Usuario
import com.example.repairme.data.repository.AdminRepository
import com.example.repairme.data.repository.DeviceRepository
import com.example.repairme.data.repository.RepairRepository
import com.example.repairme.data.repository.UserRepository
import com.example.repairme.ui.components.BaseScreen
import com.example.repairme.ui.components.NavItem
import com.example.repairme.ui.theme.ColorEstadoAsignada
import com.example.repairme.ui.theme.ColorEstadoDeclinada
import com.example.repairme.ui.theme.ColorEstadoEnReparacion
import com.example.repairme.ui.theme.ColorEstadoListaParaRecoger
import com.example.repairme.ui.theme.ColorEstadoPendiente
import com.example.repairme.ui.theme.ColorEstadoPresupuestada
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.Naranja
import com.example.repairme.ui.theme.naranjaLetras


@Composable
fun NuevaAveriaAdmin(
    onVolver: () -> Unit = {},
    onVerAverias: () -> Unit = {},
    onVerTecnicos: () -> Unit = {},
    onIrPerfil: () -> Unit = {},
    onGestionServicios: () -> Unit = {},
    onVerClientes: () -> Unit = {},
    onVerPresupuestos:()->Unit={},
    onLogOut: () -> Unit = {}
) {
    val repoReparaciones = remember { RepairRepository() }
    val repoDispositivos = remember { DeviceRepository() }
    val repoAdmin= remember { AdminRepository() }
    val repoUsuario=remember { UserRepository() }


    //var todasAverias by remember { mutableStateOf(listOf<Averia>()) }
    var busqueda by remember { mutableStateOf("") }
    var clienteSeleccionado by remember { mutableStateOf(Usuario()) }
    var listaClientes by remember { mutableStateOf(listOf<Usuario>()) }
    var marca by rememberSaveable { mutableStateOf("") }
    var modelo by rememberSaveable { mutableStateOf("") }
    var numeroSerie by remember { mutableStateOf("") }


    var añadirAveria by rememberSaveable { mutableStateOf(false) }
    var tituloAveria by rememberSaveable { mutableStateOf("") }
    var descripcionAveria by rememberSaveable { mutableStateOf("") }
    var reparaciones by remember { mutableStateOf(listOf<Averia>()) }

    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var ok by rememberSaveable { mutableStateOf(false) }
    var mostrarNuevoFormulario by remember { mutableStateOf(false) }

    var nuevoNombre by remember { mutableStateOf("") }
    var nuevoApellidos by remember { mutableStateOf("") }
    var nuevoEmail by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        repoUsuario.obtenerUsuariosTodos(
            fallo = { },
            exito = { clientes -> listaClientes = clientes }
        )
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
        onIrPerfil = onIrPerfil,
        onGestionServicios = onGestionServicios,
        onLogOut = onLogOut,
        onVolver = onVolver,

        notificationBadgeCount = 0,
        bottomNavItems = listOf(
            NavItem("Reparar", Icons.Filled.Build, onVolver),
            NavItem("Técnicos", Icons.Filled.Engineering, onVerTecnicos),
            NavItem("Clientes", Icons.Filled.Person, onVerClientes),
            NavItem("Presup.", Icons.Filled.RequestQuote, onVerPresupuestos)
        )

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
                    Text(
                        text = "Crear nueva avería",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Naranja,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )


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

                                                            ), userId = userId,
                                                        exito={ok=true},
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
                            }else {
                                //El else trabaja la posibilidad contraria, que exista y lo tenga el la tarjeta


                                if (validarCampos()) {


                                    // Crear objeto Equipo con los datos introducidos
                                    val equipo = Equipo(
                                        deviceBrand = marca.trim(),
                                        deviceModel = modelo.trim(),
                                        deviceSN = numeroSerie.trim(),
                                        //averias = listaAverias
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

                                                        ), userId = clienteSeleccionado.id,
                                                    exito = { ok = true },
                                                    fallo = { msg -> error = msg }
                                                )
                                            } else {
                                                ok = true
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
                        Text("Guardar equipo")
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