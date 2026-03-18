package com.example.repairme.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.Equipo
import com.example.repairme.data.model.EstadoAveria
import com.example.repairme.data.model.LineaPresupuesto
import com.example.repairme.data.model.Usuario
import com.example.repairme.data.repository.DeviceRepository
import com.example.repairme.data.repository.RepairRepository
import com.example.repairme.ui.screens.auth.BottomNavButton
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.Naranja
import com.example.repairme.ui.theme.grisfondo
import com.example.repairme.ui.theme.naranjaLetras
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    onAddEquipo: () -> Unit = {},
    onVolver: () -> Unit = {},
    onVerEquipos: (Equipo) -> Unit = {},
    onVerAverias: (Averia) -> Unit = {},
    onVerPresupuestos:(Averia)->Unit={},
    onGoToTestCrud: () -> Unit = {},
    onIrPerfil: () -> Unit = {},
    onIrServicios: () -> Unit = {}

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
        val repo = RepairRepository()
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
                    Log.d("Log de las averçías que recibo", "Averías Presupuestadas recibidas:${averias.size}")
                    listaPresupuestadas = averias.filter {
                        it.estado == EstadoAveria.Presupuestada.name||
                                it.estado == EstadoAveria.EnReparacion.name||
                                it.estado == EstadoAveria.Declinada.name

                    }
                }
            )
        }
    }

    Scaffold(
        containerColor = GrisFondoPantalla,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "ClearRepair",
                        color = naranjaLetras,
                        fontWeight = FontWeight.Bold,
                        // textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = grisfondo
                )
            )
        },
        bottomBar = {//RECUERDA PREGUNTARLES A LOS CHICOS SI ESTO LO HACEMOS COMPARTIBLE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavButton(icon = Icons.Filled.Computer, label = "Equipos", color = Naranja, onClick = {})
                BottomNavButton(icon = Icons.Filled.Build, label = "Reparaciones", color = Naranja, onClick = {})
                BottomNavButton(icon = Icons.Filled.RequestQuote, label = "Presupuestos", color = Naranja, onClick = {})
                BottomNavButton(icon = Icons.Filled.Person, label = "Perfil", color = Naranja, onClick = { onIrPerfil() })
                BottomNavButton(icon = Icons.Filled.Info, label = "Servicios", color = Naranja, onClick = { onIrServicios() })
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            TextButton(onClick = { onGoToTestCrud() }) {
                Text("TEST CRUD (dev)", color = Naranja)
            }

            Card(//Card de equipos
                modifier = Modifier.fillMaxWidth().clickable { equiposExpandido = !equiposExpandido },
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mis equipos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Naranja
                    )
                    Icon(
                        imageVector = Icons.Filled.Computer,
                        contentDescription = "Equipos",
                        tint = Naranja,
                        modifier = Modifier.size(40.dp)
                    )
                }

                if (equiposExpandido) {
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = { onAddEquipo() }) {
                            Text("Añadir equipo", color = Naranja)
                        }
                    }
                    Column(
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listaEquipos.forEach { equipo ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp)
                                    .clickable {
                                        //Hay que crear la función de ver equipo
                                        onVerEquipos(equipo)
                                    },
                                shape = RoundedCornerShape(9.dp),
                                border = BorderStroke(2.dp, Naranja)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "${equipo.deviceBrand} ${equipo.deviceModel}")
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

            Card(//Card de reparaciones
                modifier = Modifier.fillMaxWidth().clickable { reparacionesExpandido = !reparacionesExpandido },
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mis reparaciones",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Naranja
                    )
                    Icon(
                        imageVector = Icons.Filled.Build,
                        contentDescription = "Reparaciones",
                        tint = Naranja,
                        modifier = Modifier.size(40.dp)
                    )
                }
                if (reparacionesExpandido) {
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        //Aquí hay que poner un solicitar reparación
                        // TextButton(onClick = {onAddEquipo()}) {
                        //   Text("Añadir equipo", color = Naranja)
                        // }
                    }
                    Column(
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listaAverias.forEach { averia ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp)
                                    .clickable {
                                        onVerAverias(averia)
                                    },
                                shape = RoundedCornerShape(9.dp),
                                border = BorderStroke(2.dp, Naranja)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "${averia.equipoNombre} ${averia.tituloAveria} ")
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
            Card(//Card de presupuestos
                modifier = Modifier.fillMaxWidth().clickable { presupuestosExpandido = !presupuestosExpandido },
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mis presupuestos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Naranja
                    )
                    Icon(
                        imageVector = Icons.Filled.RequestQuote,
                        contentDescription = "Presupuestos",
                        tint = Naranja,
                        modifier = Modifier.size(40.dp)
                    )
                }//Acaba la fila
                if (presupuestosExpandido) {
                HorizontalDivider()
                Column(
                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ){

                    listaPresupuestadas.forEach { averia ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                                .clickable {
                                    dialogoAveria= averia

                                },
                            shape = RoundedCornerShape(9.dp),
                            border = BorderStroke(2.dp, Naranja)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "${averia.equipoNombre} ${averia.tituloAveria} ")
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
    }//Termina Scaffold
    @Composable
    fun DialogoPresupuestos(averia: Averia, onRechazar:()->Unit, onAceptar:()-> Unit){
        Log.d("DEBUG_DIALOG", "estado: ${averia.estado}, presupuestoAceptado: ${averia.presupuestoAceptado}")


        AlertDialog(onDismissRequest = {onRechazar()},
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

                    TextButton(onClick = { onAceptar() }) {
                        Text(text = "Confirmar")
                    }

            },


            dismissButton = {

                    TextButton(onClick = { onRechazar() }) { Text(text = "Cancelar") }

            },

            title = { Text(text = "Detalle del presupuesto") }

        )

    }
    dialogoAveria?.let {
        averia ->
        DialogoPresupuestos(
            averia=averia,
            onRechazar = {
                repo.editarAveria(
                    averiaEditada = averia.copy(
                        presupuestoAceptado = true,
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

@Preview(showSystemUi = true)
@Composable
fun UserScreenPreview() {
    UserScreen()
}