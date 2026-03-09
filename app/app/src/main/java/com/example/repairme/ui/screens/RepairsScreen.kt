package com.example.repairme.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repairme.data.model.Averia
import com.example.repairme.data.repository.RepairRepository
import com.example.repairme.ui.theme.grisfondo
import com.example.repairme.ui.theme.naranjaLetras
import com.example.repairme.data.model.Usuario
import com.example.repairme.data.repository.TecnicoRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairsScreen(
    onAddAveria: () -> Unit = {},
    onVolver: () -> Unit = {},
    onVerAvería: () -> Unit = {}
) {
    var listaAverias by remember { mutableStateOf(listOf<Averia>()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var averiaSeleccionada by remember { mutableStateOf<Averia?>(null) }
    //var averiaExpandida by remember {  }
    var listaTecnicos by remember { mutableStateOf(listOf<Usuario>()) }

    LaunchedEffect(Unit) {
        val repo = RepairRepository()
        val repo2 = TecnicoRepository()
        repo.obtenerAveriasTodas(
            fallo = { mensaje ->
                error = mensaje
                Log.d("RepairsScreen", "Error cargando averías: $mensaje")
                cargando = false
            },
            exito = { averias ->
                Log.d("RepairsScreen", "Averías recibidas: ${averias.size}")
                listaAverias = averias
                cargando = false
            }
        )
        repo2.obtenerTecnicos(
            fallo = { mensaje ->
                error = mensaje
                Log.d("RepairsScreen", "Error cargando tecnicos: $mensaje")

            },
            exito = { tecnicos ->
                Log.d("RepairsScreen", "tecnicos recibidas: ${tecnicos.size}")
                listaTecnicos = tecnicos

            }
        )
    }




    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reparaciones",
                        color = naranjaLetras,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = grisfondo)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                cargando -> CircularProgressIndicator()
                error != null -> Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
                listaAverias.isEmpty() -> Text("No hay reparaciones")
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(listaAverias) { averia ->
                            Card(modifier = Modifier.fillMaxWidth(),
                                onClick ={averiaSeleccionada=averia} ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = averia.tituloAveria,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = naranjaLetras
                                        )
                                        Text(
                                            text = averia.estado,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = averia.equipoNombre, fontSize = 14.sp)
                                    if (averia.descripcion.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = averia.descripcion,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
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

