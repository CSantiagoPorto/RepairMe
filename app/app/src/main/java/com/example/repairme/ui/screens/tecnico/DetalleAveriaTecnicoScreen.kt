package com.example.repairme.ui.screens.tecnico

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.EstadoAveria
import com.example.repairme.data.model.LineaPresupuesto
import com.example.repairme.data.repository.RepairRepository
import com.example.repairme.ui.components.BaseScreen
import com.example.repairme.ui.theme.naranjaLetras

@Composable
//Desde aqyí el técnico va a elaborar los presupuestos. Al enviar las líneas de presupuesto las guarda en la avería y cambia el estado a presupuestado
fun DetalleAveriaTecnicoScreen(
    averiaId: String,
    onVolver: () -> Unit,
    onIrHome: () -> Unit = {},
    onIrPerfil: () -> Unit = {},
    onGestionServicios: () -> Unit = {},
    onLogOut: () -> Unit = {},
    onIrNotificaciones: () -> Unit = {},
    notificacionesNoLeidas: Int = 0
) {
    val repo = remember { RepairRepository() }
    var averia by remember { mutableStateOf<Averia?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var concepto by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var precioUnidad by remember { mutableStateOf("") }
    var lineas by remember { mutableStateOf(listOf<LineaPresupuesto>()) }
    val context = LocalContext.current

    fun cargarAveria() {
        repo.obtenerAveriaId(
            averiaId = averiaId,
            fallo = { mensaje ->
                error = mensaje
                cargando = false
            },
            exito = { resultado ->
                averia = resultado
                cargando = false
            }
        )
    }

    LaunchedEffect(Unit) {
        cargarAveria()
        //Este método obtiene las averías por ID y el LaunchedEffect las carga una vez al entrar
    }

    BaseScreen(
        title = "Presupuestar reparación",
        onIrHome = onIrHome,
        onIrPerfil = onIrPerfil,
        onGestionServicios = onGestionServicios,
        onLogOut = onLogOut,
        onVolver = onVolver,
        onNotificationsClick = onIrNotificaciones,
        notificationBadgeCount = notificacionesNoLeidas
    ) { modifier ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                cargando -> CircularProgressIndicator()
                error != null -> Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error
                )

                averiaId.isEmpty() -> Text("No  existe la avería")
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item{
                            Text(text = averia?.equipoNombre?:"Desconocido")
                            Text(text = averia?.tituloAveria?:"Sin nombre")
                            Text(text = averia?.descripcion?:"No hay descripción")
                        }

                        item {
                            TextField(
                                value = concepto,
                                onValueChange = {concepto=it},
                                label = {Text("Concepto")}
                            )
                            TextField(
                                value = cantidad,
                                onValueChange = {cantidad=it},
                                label = {Text("Cantidad")}
                            )
                            TextField(
                                value = precioUnidad,
                                onValueChange = {precioUnidad=it},
                                label = {Text("Precio Unitario")}
                            )
                        }
                        item {
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = naranjaLetras,
                                        contentColor = Color.White
                                    ),
                                    onClick={
                                        var precioNumero= precioUnidad.toDoubleOrNull()?:0.0//Convierto a números los Strings del TextField
                                        var cantidadNumero= cantidad.toDoubleOrNull()?:1.0
                                        val totalFila=precioNumero*cantidadNumero
                                        val lineaEscribir= LineaPresupuesto(
                                            concepto=concepto,
                                            cantidad=cantidadNumero.toInt(),
                                            precioUnitario = precioNumero
                                        )
                                        lineas= lineas+lineaEscribir
                                        //Lineas está vacía de inicio, vamos sumando y limpiamos
                                        concepto = ""
                                        cantidad = ""
                                        precioUnidad = ""

                                    }){Text("Añadir")}
                            }
                        }

                        item {//Así la tabla tiene cabecera por si la queremos mudar de aquí
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text("Concepto", modifier = Modifier.weight(2f))
                                Text("Cant.", modifier = Modifier.weight(1f))
                                Text("Precio", modifier = Modifier.weight(1f))
                                Text("Total", modifier = Modifier.weight(1f))
                                //Text("Borrar", modifier = Modifier.weight(1f))
                            }
                        }
                        items(lineas) { linea ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(linea.concepto, modifier = Modifier.weight(2f))
                                Text("${linea.cantidad}", modifier = Modifier.weight(1f))
                                Text("${linea.precioUnitario}", modifier = Modifier.weight(1f))
                                Text("${linea.cantidad * linea.precioUnitario}", modifier = Modifier.weight(1f))

                                IconButton(
                                    onClick = { lineas = lineas - linea },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Borrar línea")
                                }


                            }
                        }
                        item {
                            val subtotal = lineas.sumOf { it.cantidad * it.precioUnitario }
                            val iva = subtotal * 0.21
                            val total = subtotal + iva

                            Text("Subtotal: $subtotal €")
                            Text("IVA (21%): $iva €")
                            Text("Total: $total €")
                        }

                        item {
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = naranjaLetras,
                                        contentColor = Color.White
                                    ),
                                    onClick = {
                                        averia?.let {

                                            var averiaPresupuestada=averia!!.copy(
                                                lineasPresupuesto=lineas,
                                                estado= EstadoAveria.Presupuestada.name
                                            )
                                            repo.editarAveria(
                                                averiaEditada = averiaPresupuestada,
                                                exito = {
                                                    Toast.makeText(context, "El presupuesto ha sido enviado con éxito",
                                                        Toast.LENGTH_LONG).show()
                                                    onVolver()
                                                },
                                                fallo = {}

                                            )



                                        }
                                    }) {
                                    Text("Enviar presupuesto")
                                }
                            }
                        }



                    }
                }


            }
        }
    }
}