package com.example.repairme.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.Equipo
import com.example.repairme.data.model.EstadoAveria
import com.example.repairme.data.repository.DeviceRepository
import com.example.repairme.data.repository.RepairRepository
import com.example.repairme.ui.components.BaseScreen
import com.example.repairme.ui.components.NavItem
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.Naranja

@Composable
fun UserScreen(
    onAddEquipo: () -> Unit = {},
    onVolver: () -> Unit = {},
    onVerEquipos: (Equipo) -> Unit = {},
    onVerAverias: (Averia) -> Unit = {},
    onVerPresupuestos: (Averia) -> Unit = {},
    onGoToTestCrud: () -> Unit = {},
    onIrPerfil: () -> Unit = {},
    onIrServicios: () -> Unit = {},
    onLogOut: () -> Unit = {}
) {
    var equiposExpandido by remember { mutableStateOf(false) }
    var reparacionesExpandido by remember { mutableStateOf(false) }
    var presupuestosExpandido by remember { mutableStateOf(false) }
    var listaEquipos by remember { mutableStateOf(listOf<Equipo>()) }
    var listaAverias by remember { mutableStateOf(listOf<Averia>()) }
    var listaPresupuestadas by remember { mutableStateOf(listOf<Averia>()) }
    var dialogoAveria by remember { mutableStateOf<Averia?>(null) }
    val repo = remember { RepairRepository() }//Necesito el repo para aceptar el presu


    LaunchedEffect(equiposExpandido) {
        val repo = DeviceRepository()
        if (equiposExpandido) {//Esot sólo se va a cargar cuando la card se abra
            repo.obtenerEquipos(
                error = { mensaje ->
                    Log.d("Si ves este mensaje es porque no está obteniendo los equipos", mensaje)
                    onVolver()
                },
                exito = { equipos ->
                    Log.d("Log de UserScreen", "Equipos recibido: ${equipos.size}")
                    listaEquipos = equipos
                }
            )
        }
    }

    LaunchedEffect(reparacionesExpandido) {
        if (reparacionesExpandido) {
            repo.obtenerAveriaUser(
                fallo = { mensaje ->
                    Log.d("Si ves este mensaje es porque no está obteniendo las averías", mensaje)
                    onVolver()
                },
                exito = { averias ->
                    Log.d("Log de las averçías que recibo", "Averías recibidas:${averias.size}")
                    listaAverias = averias
                }
            )
        }
    }
    LaunchedEffect(presupuestosExpandido) {
        val repo = RepairRepository()
        if (presupuestosExpandido) {
            repo.obtenerAveriaUser(
                fallo = { mensaje ->
                    Log.d("Si ves este mensaje es porque no está obteniendo las averías presupuestadas", mensaje)
                    onVolver()
                },
                exito = { averias ->
                    listaPresupuestadas = averias
                }
            )
        }
    }

    // 2. Configuramos los botones de la barra inferior vinculados a los estados de expansión
    val itemsNavegacion = listOf(
        NavItem("Equipos", Icons.Filled.Computer, {
            equiposExpandido = !equiposExpandido
            reparacionesExpandido = false
            presupuestosExpandido = false
        }),
        NavItem("Reparar", Icons.Filled.Build, {
            reparacionesExpandido = !reparacionesExpandido
            equiposExpandido = false
            presupuestosExpandido = false
        }),
        NavItem("Presus", Icons.Filled.RequestQuote, {
            presupuestosExpandido = !presupuestosExpandido
            equiposExpandido = false
            reparacionesExpandido = false
        })
    )

    // 3. Aplicamos el modelo BaseScreen (Logo arriba + Acciones perfil/servicios + BottomNav)
    BaseScreen(
        title = "ClearRepair",
        onIrPerfil = onIrPerfil,
        onGestionServicios = onIrServicios,
        onLogOut = onLogOut,
        bottomNavItems = itemsNavegacion
    ) { modifier ->
        
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(GrisFondoPantalla)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Test Crud card para testing
            /*
            TextButton(onClick = { onGoToTestCrud() }) {
                Text("TEST CRUD (dev)", color = Naranja)
            }
            */

            // Card de Equipos
            UserCard(
                titulo = "Mis equipos",
                icono = Icons.Filled.Computer,
                onClick = { equiposExpandido = !equiposExpandido }
            )

            if (equiposExpandido) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        TextButton(onClick = { onAddEquipo() }) {
                            Text("Añadir equipo", color = Naranja)
                        }
                        listaEquipos.forEach { equipo ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable { onVerEquipos(equipo) },
                                shape = RoundedCornerShape(9.dp),
                                border = BorderStroke(1.dp, Naranja)
                            ) {
                                Text(
                                    text = "${equipo.deviceBrand} ${equipo.deviceModel}",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            /*Text(
                            //Ahora mismo los equipos se muestran feos, en un text
                            //Esto mejor convertirlo en otra card
                            text = "${equipo.deviceBrand} ${equipo.deviceModel}"
                        ) */
                        }
                    }
                }
            }

            // Card de Reparaciones
            UserCard(
                titulo = "Mis reparaciones",
                icono = Icons.Filled.Build,
                onClick = { reparacionesExpandido = !reparacionesExpandido }
            )

            if (reparacionesExpandido) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        listaAverias.forEach { averia ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable { onVerAverias(averia) },
                                shape = RoundedCornerShape(9.dp),
                                border = BorderStroke(1.dp, Naranja)
                            ) {
                                Text(
                                    text = "${averia.equipoNombre} ${averia.tituloAveria}",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            /*Text(
                            //Ahora mismo los equipos se muestran feos, en un text
                            //Esto mejor convertirlo en otra card
                            text = "${equipo.deviceBrand} ${equipo.deviceModel}"
                        ) */
                        }
                    }
                }
            }

            // Card de Presupuestos
            UserCard(
                titulo = "Mis presupuestos",
                icono = Icons.Filled.RequestQuote,
                onClick = { presupuestosExpandido = !presupuestosExpandido }
            )

            if (presupuestosExpandido) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        listaPresupuestadas.forEach { averia ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable { dialogoAveria = averia },
                                shape = RoundedCornerShape(9.dp),
                                border = BorderStroke(1.dp, Naranja)
                            ) {
                                Text(
                                    text = "${averia.equipoNombre} ${averia.tituloAveria}",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        /*Text(
                        //Ahora mismo los equipos se muestran feos, en un text
                        //Esto mejor convertirlo en otra card
                        text = "${equipo.deviceBrand} ${equipo.deviceModel}"
                    ) */
                    }
                }
            }
        }
    }
    //Termina Scaffold
    @Composable
    fun DialogoPresupuestos(averia: Averia, onCerrar:()->Unit, onRechazar:()->Unit, onAceptar:()-> Unit){
        Log.d("DEBUG_DIALOG", "estado: ${averia.estado}, presupuestoAceptado: ${averia.presupuestoAceptado}")


        AlertDialog(onDismissRequest = {onCerrar()},
            text = {Column() {
                averia.lineasPresupuesto.forEach {
                    linea->
                    Text("${linea.concepto}, ${linea.cantidad}, ${linea.precioUnitario}")


                }
                val subtotal = averia.lineasPresupuesto.sumOf { it.cantidad * it.precioUnitario }
                val iva = subtotal * 0.21
                Text("Subtotal: $subtotal €")
                Text("IVA: $iva €")
                Text("Total: ${subtotal + iva} €")

            }},

            confirmButton = {
                if (averia.estado == EstadoAveria.Presupuestada.name) {
                    TextButton(onClick = { onAceptar() }) {
                        Text(text = "Confirmar")
                    }
                }
            },
            dismissButton = {
                if (averia.estado == EstadoAveria.Presupuestada.name) {
                    TextButton(onClick = { onRechazar() }) { Text(text = "Cancelar") }
                }
            },

            title = { Text(text = "Detalle del presupuesto") }

        )

    }
    dialogoAveria?.let {
        averia ->
        DialogoPresupuestos(
            averia=averia,
            onCerrar = { dialogoAveria = null },
            onRechazar = {
                repo.editarAveria(
                    averiaEditada = averia.copy(
                        presupuestoAceptado = false,
                        estado= EstadoAveria.Declinada.name
                ),
                    exito = {
                        listaPresupuestadas=listaPresupuestadas.filter { it.id!=averia.id }
                        dialogoAveria=null
                    },
                    fallo={dialogoAveria=null}
                )
            },
            onAceptar = {
                repo.editarAveria(
                    averiaEditada = averia.copy(
                        presupuestoAceptado = true,
                        estado= EstadoAveria.EnReparacion.name
                    ),
                    exito = {
                        listaPresupuestadas=listaPresupuestadas.filter { it.id!=averia.id }
                        dialogoAveria=null
                    },
                    fallo={dialogoAveria=null}
                )
            }//Hay que cerrar el dialogo
        )
    }
}

@Composable
fun UserCard(titulo: String, icono: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = titulo, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Naranja)
            Icon(imageVector = icono, contentDescription = titulo, tint = Naranja, modifier = Modifier.size(40.dp))
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun UserScreenPreview() {
    UserScreen()
}
