package com.example.repairme.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.EstadoAveria
import com.example.repairme.data.repository.RepairRepository
import androidx.compose.ui.tooling.preview.Preview
import com.example.repairme.data.model.Usuario
import com.example.repairme.data.repository.UserRepository
import com.example.repairme.ui.components.BaseScreen
import com.example.repairme.ui.components.NavItem
import com.example.repairme.ui.theme.GrisFondoPantalla


val AzulAdmin = Color(0xFF1B3A6B)
val GrisFondo = Color(0xFFF2F4F8)
val NaranjaAdmin = Color(0xFFF97316)

@Composable
fun PresupuestoQueVeElAdmin(
    onVolver: () -> Unit = {},
    onVerAverias: () -> Unit = {},
    onVerTecnicos: () -> Unit = {},
    onIrPerfil: () -> Unit = {},
    onGestionServicios: () -> Unit = {},
    onVerClientes: () -> Unit = {},
    onLogOut: () -> Unit = {}

) {
    val repo = remember { RepairRepository() }
    var todasAverias by remember { mutableStateOf(listOf<Averia>()) }
    var busqueda by remember { mutableStateOf("") }
    val userRepo = remember { UserRepository() }
    var mapaUsuarios by remember { mutableStateOf(mapOf<String, Usuario>()) }



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

    //En la primera sección quiero que enseñe las que están pendientes de aceptar
    //Así el admin puede llamar al cliente
    val pendientes = todasAverias
        .filter { it.estado == EstadoAveria.Presupuestada.name }
        .sortedBy { it.createdAt }


    val resultadosBusqueda = if (busqueda.isBlank()) emptyList() else
        todasAverias.filter { averia ->
            val cliente = mapaUsuarios[averia.userId]
            cliente?.name?.contains(busqueda, ignoreCase = true) == true ||
                    cliente?.apellidos?.contains(busqueda, ignoreCase = true) == true
        }


    val hacerUnMes = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)


    val presupuestadasEsteMes = todasAverias.filter { averia ->
        android.util.Log.d("FILTRO_MES", "id=${averia.id} createdAt=${averia.createdAt} lineas=${averia.lineasPresupuesto.size}")

        averia.lineasPresupuesto.isNotEmpty() && averia.createdAt >= hacerUnMes
    }



    BaseScreen (
        title = "Presupuestos",
        onIrPerfil = onIrPerfil,
        onGestionServicios = onGestionServicios,
        onLogOut = onLogOut,
        bottomNavItems = listOf(
            NavItem("Reparar", Icons.Filled.Build, onVerAverias),
            NavItem("Técnicos", Icons.Filled.Engineering, onVerTecnicos),
            NavItem("Clientes", Icons.Filled.Person, onVerClientes)
        )

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
                    text = "BUSCAR POR CLIENTE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9CA3AF),
                    letterSpacing = 0.06.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    label = { Text("Nombre del cliente") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )


            }
            if(busqueda.isBlank()){
                item {
                    Text(
                        text = "PENDIENTES DE ACEPTAR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9CA3AF),
                        letterSpacing = 0.06.sp
                    )
                }
            }


            if (busqueda.isBlank()){
                items(pendientes) { averia ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(2.dp, Color(0xFFFED7AA))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = averia.tituloAveria,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = averia.estado,
                                fontSize = 12.sp,
                                color = Color(0xFFB45309),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            items(resultadosBusqueda) { averia ->
                val cliente = mapaUsuarios[averia.userId]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(0.5.dp, Color(0xFFE5E7EB))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = averia.tituloAveria,
                            fontSize = 17.sp,
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
                            text = averia.estado,
                            fontSize = 12.sp,
                            color = NaranjaAdmin,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
            item {

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "PRESUPUESTADAS ESTE MES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9CA3AF),
                    letterSpacing = 0.06.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(presupuestadasEsteMes) { averia ->
                val cliente = mapaUsuarios[averia.userId]
                val subtotal = averia.lineasPresupuesto.sumOf { it.cantidad * it.precioUnitario }
                val total = subtotal * 1.21
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(2.dp, Color(0xFFFED7AA))
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
                            text = "Total: ${"%.2f".format(total)} €",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaranjaAdmin,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }




        }
    }

}
@Preview(showSystemUi = true)
@Composable
fun PresupuestoQueVeElAdminPreview() {
    PresupuestoQueVeElAdmin(onVolver={})
}



