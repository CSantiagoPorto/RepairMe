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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.EstadoAveria
import com.example.repairme.data.model.Tecnico
import com.example.repairme.data.repository.RepairRepository
import com.example.repairme.ui.theme.grisfondo
import com.example.repairme.ui.theme.naranjaLetras
import com.example.repairme.data.model.Usuario
import com.example.repairme.data.repository.TecnicoRepository
import com.example.repairme.ui.components.BaseScreen
import com.example.repairme.ui.components.NavItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairsScreen(
    onAddAveria: () -> Unit = {},
    onVolver: () -> Unit = {},
    onVerAveria: () -> Unit = {},
    onVerTecnicos: () -> Unit = {},
    onIrPerfil: () -> Unit = {},
    onGestionServicios: () -> Unit = {},
    onVerClientes: () -> Unit = {},
    onVerPresupuestos: () -> Unit = {},
    onLogOut: () -> Unit = {}




) {
    var listaAverias by remember { mutableStateOf(listOf<Averia>()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var averiaSeleccionada by remember { mutableStateOf<Averia?>(null) }

    var listaTecnicos by remember { mutableStateOf(listOf<Usuario>()) }

    val repo =remember {  RepairRepository() }
    val repo2 = remember { TecnicoRepository() }
    var averiaParaCambiarTecnico by remember { mutableStateOf<Averia?>(null) }

    fun cargarAverias() {
        repo.obtenerAveriasTodas(
            fallo = { mensaje -> error = mensaje
                    cargando=false},
            exito = { averias -> listaAverias = averias
            cargando=false}
        )
    }

    LaunchedEffect(Unit) {

      cargarAverias()
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






    BaseScreen(
        title = "Asignar técnico a una reparación",
        onIrPerfil = onIrPerfil,
        onGestionServicios = onGestionServicios,
        onLogOut = onLogOut,
        bottomNavItems = listOf(
            NavItem("Reparar", Icons.Filled.Build, onVolver),
            NavItem("Técnicos", Icons.Filled.Engineering, onVerTecnicos),
            NavItem("Clientes", Icons.Filled.Person, onVerClientes),
            NavItem("Presupuestos", Icons.Filled.RequestQuote, onVerPresupuestos)
        )

    ) { modifier ->
        Column(
            modifier = modifier
                .fillMaxSize()
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
                                onClick ={ if(averia.tecnicoId.isBlank()){
                                    averiaSeleccionada=averia
                                }else{averiaParaCambiarTecnico=averia}
                                } ) {
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
        averiaSeleccionada?.let {//Let hace que sólo se ejecute si no es null
                averia->
            DialogoAsignar(
                averia=averia,
                tecnicos = listaTecnicos,
                onRechazar = { averiaSeleccionada=null },
                onAceptar = {tecnicoId->
                    val averiaModificada=averia.copy(
                        tecnicoId=tecnicoId,
                        estado = EstadoAveria.Asignada.name
                    )
                    repo.editarAveria(
                        averiaEditada = averiaModificada,
                        exito={ averiaSeleccionada=null
                              cargarAverias()},
                        fallo = {}
                    )

                }
            )
        }

        averiaParaCambiarTecnico?.let {
            averia->
            DialogoCambiarTecnico(
                averia=averia,
                tecnicos = listaTecnicos,
                onRechazar = {averiaParaCambiarTecnico=null},
                onAceptar = {
                    averiaParaCambiarTecnico=null
                    averiaSeleccionada=averia
                }
            )
        }
    }
}
@Composable
fun DialogoAsignar(averia: Averia?, tecnicos:List<Usuario>, onRechazar:()->Unit, onAceptar:(String)-> Unit){
    var tecnicoSeleccionado by remember { mutableStateOf<String?>(null) }

       AlertDialog(onDismissRequest = {onRechazar()},
           text = {Column() {
               tecnicos.forEach { tecnico->
                   TextButton(onClick = {tecnicoSeleccionado=tecnico.id}) {
                       Text(text = tecnico.name)
                   }
               }
           }},

           confirmButton = {
               TextButton(onClick = {
                   tecnicoSeleccionado?.let { onAceptar(it) }

               }) {Text(text ="Confirmar" ) }

           },
           dismissButton = {
               TextButton(onClick = {
                   onRechazar()
               }) {Text(text = "Cancelar") }
           },
           title =  {Text(text = "Asigne un técnico a la reparación")}
       )

}

@Composable
fun DialogoCambiarTecnico(averia: Averia?, tecnicos:List<Usuario>, onRechazar:()->Unit, onAceptar:()-> Unit){
    var tecnicoSeleccionado by remember { mutableStateOf<String?>(null) }
    AlertDialog(


        onDismissRequest = {onRechazar()},

        confirmButton = {
            TextButton(onClick = {
               onAceptar()
            }) {Text(text ="Confirmar" ) }
        },
        dismissButton = {
            TextButton(onClick = {
                onRechazar()
            }) {Text(text = "Cancelar") }
        },
        title =  {Text(text = "Esta reparación ya tiene técnico asignado \n Desea cambiarlo? \n El técnico asignado actualmente es: ${tecnicos.find { it.id == averia?.tecnicoId }?.name}") },

    )
}

