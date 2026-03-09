package com.example.repairme.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.repairme.data.model.Averia
import com.example.repairme.data.repository.RepairRepository
import com.example.repairme.ui.theme.grisfondo
import com.example.repairme.ui.theme.naranjaLetras

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairsScreen(
    onAddAveria: ()-> Unit={},
    onVolver:()->Unit={},
    onVerAvería:()->Unit={}
) {

      //  var reparacionesExpandido by remember { mutableStateOf(false) }
        var listaAverias by remember { mutableStateOf(listOf<Averia>()) }
        var cargando by remember { mutableStateOf(true) }
        var error by remember {mutableStateOf<String?>(null)}



        LaunchedEffect(Unit) {//Sólo se ejecuta 1 vez
            val repo = RepairRepository()
            repo.obtenerAveriasTodas(
                fallo = { mensaje ->
                    error=mensaje
                    Log.d("Si ves este mensaje es porque no está obteniendo las averías", mensaje)
                    cargando=false
                    //onVolver()
                },
                exito = { averias ->
                    Log.d("Log de las averçías que recibo", "Averías recibidas:${averias.size}")
                    listaAverias = averias
                    cargando=false

                }
            )

        }


        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("ClearRepair",
                        color = naranjaLetras,
                        fontWeight = FontWeight.Bold,
                        )
                            },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = grisfondo
                    )

                )
            }//Cierra la TopBar
            /* Pregunta si quieren esto aquí
    bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavButton(icon = Icons.Filled.Computer, label = "Mis equipos", color = Naranja, onClick = {})
                BottomNavButton(icon = Icons.Filled.Build, label = "Mis reparaciones", color = Naranja, onClick = {})
                BottomNavButton(icon = Icons.Filled.Notifications, label = "Mis notificaciones", color = Naranja, onClick = {})
                BottomNavButton(icon = Icons.Filled.Person, label = "Mi Perfil", color = Naranja, onClick = {})
            }
        }*/
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Reparaciones",
                    modifier = Modifier.padding(innerPadding)

                )
                if (cargando ==true){
                    CircularProgressIndicator()
                }
                else if(error!=null){Text(text = "Hay un error")}
                else if(listaAverias.isNotEmpty()){
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(listaAverias){averia->
                            Text(text = "Avería ${averia.tituloAveria}")
                        }
                    }


                }




            }



        }


}