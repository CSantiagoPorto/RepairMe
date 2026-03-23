package com.example.repairme.ui.screens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.EstadoAveria
import com.example.repairme.data.model.Usuario
import com.example.repairme.data.repository.RepairRepository
import com.example.repairme.data.repository.UserRepository
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.Naranja
import com.example.repairme.ui.theme.naranjaLetras
import com.example.repairme.utils.generarPdf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresupuestoDetalleScreen(
    averiaId: String,
    onVolver: () -> Unit
){
    //Para los pdfs necesito esta variable para abrir el directorio
    var context= LocalContext.current
    var error by remember { mutableStateOf<String?>(null) }

    //Voy a necesitar métodos de dos repos usuarios y de repairs
    val repairRepo = remember { RepairRepository() }
    val userRepo = remember { UserRepository() }
    var averia by remember { mutableStateOf<Averia?>(null) }
    var cliente by remember { mutableStateOf<Usuario?>(null) }
    var tecnico by remember { mutableStateOf<Usuario?>(null) }
    //Le meto un indicador de carga por las pruebas

    var cargando by remember { mutableStateOf(true) }
    fun cargarAveriaUserTecnico() {
        repairRepo.obtenerAveriaId(
            averiaId = averiaId,
            fallo = { mensaje ->
                error = mensaje
                cargando = false
            },
            exito = { resultado ->
                averia = resultado
                userRepo.obtenerCualquierUsuarioPorId(
                    fallo = {},
                    id = resultado.userId,
                    exito = { usuario -> cliente = usuario }
                )
                userRepo.obtenerCualquierUsuarioPorId(
                    fallo = {},
                    id = resultado.tecnicoId,
                    exito = { usuariot -> tecnico = usuariot }
                )
                cargando = false
            }
        )
    }

    LaunchedEffect(Unit) {
        Log.d("PRESU_DETALLE DEVUELTA",": $averiaId")
        cargarAveriaUserTecnico()
    }
    Scaffold(containerColor = GrisFondoPantalla,topBar ={
        TopAppBar(title = {
            Text("Detalle del presupuesto",
                color = naranjaLetras,
                fontWeight = FontWeight.Bold)
        },
            navigationIcon = {
                IconButton(onClick = onVolver) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        )
    }) {  innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            when {
                cargando -> CircularProgressIndicator()
                error != null -> Text(
                    text = "Error: $error\nID recibido: '$averiaId'",
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
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = "Datos del cliente", fontWeight = FontWeight.Bold, color = naranjaLetras)

                                    Text(text = cliente?.name?:"No se encontró  en nombre")
                                    Text(text = cliente?.apellidos ?:"No se encontraron los apellidos")
                                    Text(text = cliente?.dni?:"No se encontró ningún dni asociado")
                                }

                            }//Acaba card cliente

                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {

                                    Text(text = "Datos del técnico", fontWeight = FontWeight.Bold, color = naranjaLetras )
                                    Text(text = tecnico?.name?:"No se encontró el técnico")

                                }
                            }//Termina card técnico

                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = "Datos de la avería", fontWeight = FontWeight.Bold, color = naranjaLetras )
                                    Text(text = averia?.equipoNombre?:"Desconocido")
                                    Text(text = averia?.tituloAveria?:"Sin nombre")
                                    Text(text = averia?.descripcion?:"No hay descripción")



                                }
                            }





                        }
                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column() {
                                    Text(text = "Datos de la avería", fontWeight = FontWeight.Bold, color = naranjaLetras )
                                    averia!!.lineasPresupuesto.forEach{
                                        linea->
                                        Row() {
                                            Text(linea.concepto)
                                            Text("${linea.cantidad * linea.precioUnitario} €")

                                        }

                                    }
                                    val subtotal = averia!!.lineasPresupuesto.sumOf { it.cantidad * it.precioUnitario }
                                    val iva = subtotal * 0.21
                                    Text("Subtotal: $subtotal €")
                                    Text("IVA: $iva €")
                                    Text("Total: ${subtotal + iva} €")

                                }
                            }
                        }




                        item {
                            if (averia?.estado == EstadoAveria.Presupuestada.name) {

                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                                TextButton(onClick = {
                                    repairRepo.editarAveria(
                                        averiaEditada = averia!!.copy(
                                            presupuestoAceptado = true,
                                            estado = EstadoAveria.EnReparacion.name
                                        ),
                                        exito = {
                                            onVolver()


                                        },
                                        fallo = {
                                            //ACUERDATE DE PONER MENSAJE
                                        }
                                    )

                                },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Naranja,
                                        contentColor = Color.White
                                    )
                                    ) {
                                    Text(text = "Aceptar")
                                }
                                TextButton(onClick = {
                                    repairRepo.editarAveria(
                                        averiaEditada = averia!!.copy(
                                            presupuestoAceptado = true,
                                            estado = EstadoAveria.Declinada.name
                                        ),
                                        exito = {


                                        },
                                        fallo = {}
                                    )

                                },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Naranja,
                                        contentColor = Color.White
                                    )
                                    ) {
                                    Text(text = "Rechazar")
                                }




                            }
                        }
                        }
                       if(averia?.presupuestoAceptado==true){
                           item {
                               Row(modifier = Modifier.fillMaxWidth(),
                                   horizontalArrangement = Arrangement.Center) {
                                   Button(onClick = {
                                       val archivo= generarPdf(
                                           context=context,
                                           averia=averia!!,
                                           cliente=cliente!!,
                                           tecnico=tecnico!!
                                       )
                                       val uri = FileProvider.getUriForFile(
                                           context,
                                           "${context.packageName}.provider",
                                           archivo
                                       )
                                       val intent = Intent(Intent.ACTION_VIEW).apply {
                                           setDataAndType(uri, "application/pdf")
                                           addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                       }
                                       context.startActivity(intent)


                                   },
                                       colors = ButtonDefaults.buttonColors(
                                           containerColor = Naranja,
                                           contentColor = Color.White
                                       )

                                   ) {
                                       Text("Generar presupuesto en pdf")
                                   }
                               }
                           }
                       }



                    }
                }


            }
        }
    }





}