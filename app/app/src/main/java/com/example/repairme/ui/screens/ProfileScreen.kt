package com.example.repairme.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.repairme.R
import com.example.repairme.data.model.Usuario
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.grisfondo
import com.example.repairme.ui.theme.naranjaLetras
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onVolver: () -> Unit = {}
) {
    val context = LocalContext.current

    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            cargando = false
            Toast.makeText(context, "No hay usuario autenticado", Toast.LENGTH_SHORT).show()
        } else {
            val ref = FirebaseDatabase.getInstance(
                "https://repairme-956fd-default-rtdb.europe-west1.firebasedatabase.app"
            ).getReference("users").child(uid)

            ref.get()
                .addOnSuccessListener { snapshot ->
                    val user = snapshot.getValue(Usuario::class.java)
                    usuario = user?.copy(id = uid)
                    cargando = false
                }
                .addOnFailureListener {
                    cargando = false
                    Toast.makeText(context, "No se ha podido cargar el perfil", Toast.LENGTH_SHORT).show()
                }
        }
    }

    Scaffold(
        containerColor = GrisFondoPantalla,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi perfil", color = naranjaLetras) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.clear_repair_mini),
                contentDescription = "Logo ClearRepair",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            when {
                cargando -> {
                    Text("Cargando perfil...")
                }

                usuario == null -> {
                    Text("No se ha encontrado la información del usuario")
                }

                else -> {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DatoPerfil("Nombre", usuario?.name ?: "")
                            DatoPerfil("Apellidos", usuario?.apellidos ?: "")
                            DatoPerfil("Email", usuario?.email ?: "")
                            DatoPerfil("Teléfono", usuario?.phone ?: "")
                            DatoPerfil("Dirección", usuario?.direccion ?: "")
                            DatoPerfil("Código postal", usuario?.codigoPostal ?: "")
                            DatoPerfil("Localidad", usuario?.localidad ?: "")
                            DatoPerfil("DNI", usuario?.dni ?: "")
                            DatoPerfil("Rol", usuario?.role ?: "")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DatoPerfil(
    titulo: String,
    valor: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = titulo)
        Text(text = if (valor.isBlank()) "No disponible" else valor)
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}