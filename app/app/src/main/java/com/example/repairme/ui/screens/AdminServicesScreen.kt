package com.example.repairme.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.repairme.data.model.Servicio
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.Naranja
import com.example.repairme.ui.theme.grisfondo
import com.example.repairme.ui.theme.naranjaLetras
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminServicesScreen(
    onVolver: () -> Unit = {}
) {

    val context = LocalContext.current

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var servicioEditandoId by remember { mutableStateOf("") }
    var listaServicios by remember { mutableStateOf(listOf<Servicio>()) }

    val db = FirebaseDatabase.getInstance(
        "https://repairme-956fd-default-rtdb.europe-west1.firebasedatabase.app"
    )
    val refServicios = db.getReference("services")

    fun limpiarFormulario() {
        titulo = ""
        descripcion = ""
        servicioEditandoId = ""
    }

    fun cargarServicios() {
        refServicios.get().addOnSuccessListener { snapshot ->
            val lista = mutableListOf<Servicio>()

            for (child in snapshot.children) {
                val servicio = child.getValue(Servicio::class.java)
                val id = child.key ?: ""

                if (servicio != null) {
                    lista.add(servicio.copy(id = id))
                }
            }

            listaServicios = lista
        }
    }

    LaunchedEffect(Unit) {
        cargarServicios()
    }

    Scaffold(
        containerColor = GrisFondoPantalla,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Gestionar servicios", color = naranjaLetras) },
                navigationIcon = {
                    TextButton(onClick = { onVolver() }) {
                        Text("Volver", color = naranjaLetras)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = grisfondo
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = if (servicioEditandoId.isBlank()) "Nuevo servicio" else "Editar servicio",
                        fontWeight = FontWeight.SemiBold,
                        color = Naranja
                    )

                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                if (titulo.isBlank() || descripcion.isBlank()) {
                                    Toast.makeText(context, "Rellena título y descripción", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                if (servicioEditandoId.isBlank()) {
                                    val id = refServicios.push().key ?: return@Button

                                    val servicio = Servicio(
                                        id = id,
                                        titulo = titulo,
                                        descripcion = descripcion,
                                        activo = true
                                    )

                                    refServicios.child(id).setValue(servicio)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Servicio guardado", Toast.LENGTH_SHORT).show()
                                            limpiarFormulario()
                                            cargarServicios()
                                        }
                                } else {
                                    val updates = mapOf(
                                        "titulo" to titulo,
                                        "descripcion" to descripcion
                                    )

                                    refServicios.child(servicioEditandoId).updateChildren(updates)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Servicio actualizado", Toast.LENGTH_SHORT).show()
                                            limpiarFormulario()
                                            cargarServicios()
                                        }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Naranja
                            )
                        ) {
                            Text(if (servicioEditandoId.isBlank()) "Guardar servicio" else "Actualizar servicio")
                        }

                        if (servicioEditandoId.isNotBlank()) {
                            Button(
                                onClick = { limpiarFormulario() }
                            ) {
                                Text("Cancelar")
                            }
                        }
                    }
                }
            }

            Text(
                text = "Servicios guardados",
                fontWeight = FontWeight.SemiBold,
                color = Naranja
            )

            listaServicios.forEach { servicio ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = servicio.titulo,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = servicio.descripcion)
                        }

                        Row {
                            IconButton(
                                onClick = {
                                    servicioEditandoId = servicio.id
                                    titulo = servicio.titulo
                                    descripcion = servicio.descripcion
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Editar",
                                    tint = Naranja,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    refServicios.child(servicio.id).removeValue()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Servicio eliminado", Toast.LENGTH_SHORT).show()
                                            if (servicioEditandoId == servicio.id) {
                                                limpiarFormulario()
                                            }
                                            cargarServicios()
                                        }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Naranja,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminServicesScreenPreview() {
    AdminServicesScreen()
}