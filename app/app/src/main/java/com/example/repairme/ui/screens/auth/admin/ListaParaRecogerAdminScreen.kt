package com.example.repairme.ui.screens.auth.admin

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.EstadoAveria
import com.example.repairme.data.model.Usuario
import com.example.repairme.data.repository.RepairRepository
import com.example.repairme.data.repository.UserRepository
import com.example.repairme.ui.components.BaseScreen
import com.example.repairme.ui.components.NavItem
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.botonNaranja
import com.example.repairme.utils.generarFactura

@Composable
fun VerListasRecoger(
    onIrHome: () -> Unit = {},
    onVolver: () -> Unit = {},
    onIrPerfil: () -> Unit = {},
    onGestionServicios: () -> Unit = {},
    onIrNotificaciones: () -> Unit = {},
    onLogOut: () -> Unit = {}
){
    val repo = remember { RepairRepository() }
    var todasAverias by remember { mutableStateOf(listOf<Averia>()) }
    var busqueda by remember { mutableStateOf("") }
    val userRepo = remember { UserRepository() }
    var mapaUsuarios by remember { mutableStateOf(mapOf<String, Usuario>()) }
    var notificacionesNoLeidas by remember { mutableStateOf(0) }
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
                    border = BorderStroke(2.dp, if(
                        averia.fechaListo<=hace10dias
                    ){
                        Color.Red
                    }else{botonNaranja}
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
                                    averia=averia,
                                    cliente= cliente!!
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



