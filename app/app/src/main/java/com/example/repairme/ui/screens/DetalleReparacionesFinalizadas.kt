package com.example.repairme.ui.screens

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
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.EstadoAveria
import com.example.repairme.data.model.LineaPresupuesto
import com.example.repairme.data.model.Usuario
import com.example.repairme.data.repository.RepairRepository
import com.example.repairme.data.repository.UserRepository
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.Naranja
import com.example.repairme.ui.theme.botonNaranja
import com.example.repairme.ui.theme.naranjaLetras
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleReparacionesFinalizadas(
    averiaId: String,
    onVolver: () -> Unit
) {
    var error by remember { mutableStateOf<String?>(null) }

    //Voy a necesitar métodos de dos repos usuarios y de repairs
    val repairRepo = remember { RepairRepository() }
    val userRepo = remember { UserRepository() }
    //Necesito traerme de firebase el técnico, la avería y el cliente

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
    fun pasarFechaEntregaAString(timestamp: Long):String{
        if(timestamp!=0L){
           return java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
               .format(java.util.Date(timestamp))
        }else{return "No se encontró fecha de entrega"}
    }

    LaunchedEffect(Unit) {
        cargarAveriaUserTecnico()
    }
    Scaffold(containerColor = GrisFondoPantalla,topBar ={
        TopAppBar(title = {
            Text("Detalle de la reparaciones finalizadas",
                color = naranjaLetras,
                fontWeight = FontWeight.Bold)
        },
            navigationIcon = {
                IconButton(onClick = onVolver) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        )
    }) { //Soy el contenido del SCAFFOLD}
            innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                                    Text(text = "Fecha de entrega : ${pasarFechaEntregaAString(averia?.fechaEntrega ?: 0L)}")


                                }
                            }



                          //  Hay que añadir la fecha de entrega y hay que convertirla antes porque es un Long

                        }


                        item {
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center) {
                                Button(onClick = {

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

                        item {
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center) {
                                Button(onClick = {

                                },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Naranja,
                                        contentColor = Color.White
                                    )) {
                                    Text("Generar factura en pdf")
                                }
                            }
                        }



                    }
                }


            }
        }
    }

}
