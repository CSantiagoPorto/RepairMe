package com.example.repairme.ui.screens.common

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.material3.OutlinedTextField
import com.example.repairme.data.repository.AdminRepository


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onVolver: () -> Unit = {},
    onLogOut: () -> Unit = {}
) {
    val context = LocalContext.current

    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var cargando by remember { mutableStateOf(true) }
    val repoAdmin = remember { AdminRepository(context) }

    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        val emailActual = auth.currentUser?.email ?: ""

        Log.d("PROFILE", "UID actual: $uid")
        Log.d("PROFILE", "EMAIL actual: $emailActual")

        if (uid == null) {
            cargando = false
            Toast.makeText(context, "No hay usuario autenticado", Toast.LENGTH_SHORT).show()
        } else {
            val db = FirebaseDatabase.getInstance(
                "https://repairme-956fd-default-rtdb.europe-west1.firebasedatabase.app"
            )

            db.getReference("users").child(uid).get()
                .addOnSuccessListener { snapshot ->
                    val user = snapshot.getValue(Usuario::class.java)
                    usuario = user?.copy(id = uid)

                    Log.d("PROFILE", "Usuario cargado: $usuario")
                    Log.d("PROFILE", "ROLE actual leído: ${usuario?.role}")

                    cargando = false
                }
                .addOnFailureListener {
                    cargando = false
                    Toast.makeText(context, "No se ha podido cargar el perfil", Toast.LENGTH_SHORT).show()
                }

            // LOGS TEMPORALES para comprobar roles de usuarios concretos
            db.getReference("users").child("6CHNOTVoSpVn6nV5KaolEONbU443").get()
                .addOnSuccessListener { snapshot ->
                    Log.d("PROFILE", "Tecnico por UID 6CHNOTVoSpVn6nV5KaolEONbU443 -> role=${snapshot.child("role").value}, email=${snapshot.child("email").value}")
                }

            db.getReference("users").child("8gmq1ikJbChuASVEGZB3U3fAsT82").get()
                .addOnSuccessListener { snapshot ->
                    Log.d("PROFILE", "User por UID 8gmq1ikJbChuASVEGZB3U3fAsT82 -> role=${snapshot.child("role").value}, email=${snapshot.child("email").value}")
                }

            db.getReference("users").orderByChild("email").equalTo("jolo@correo.es").get()
                .addOnSuccessListener { snapshot ->
                    for (child in snapshot.children) {
                        Log.d("PROFILE", "Tecnico por email jolo@correo.es -> uid=${child.key}, role=${child.child("role").value}")
                    }
                }

            db.getReference("users").orderByChild("email").equalTo("admin@correo.es").get()
                .addOnSuccessListener { snapshot ->
                    for (child in snapshot.children) {
                        Log.d("PROFILE", "Admin por email admin@correo.es -> uid=${child.key}, role=${child.child("role").value}")
                    }
                }

            db.getReference("users").orderByChild("email").equalTo("alex@testing.com").get()
                .addOnSuccessListener { snapshot ->
                    for (child in snapshot.children) {
                        Log.d("PROFILE", "User por email alex@testing.com -> uid=${child.key}, role=${child.child("role").value}")
                    }
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
                    when (usuario?.role?.lowercase()) {
                        "user" -> PerfilUser(usuario = usuario!!, onVolver = onVolver, onLogOut = onLogOut)
                        "tecnico" -> PerfilTecnico(usuario = usuario!!, onVolver = onVolver)
                        "admin" -> PerfilAdmin(usuario = usuario!!, onVolver = onVolver)
                        else -> {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    DatoPerfil("Nombre", usuario?.name ?: "")
                                    DatoPerfil("Email", usuario?.email ?: "")
                                    DatoPerfil("Rol", usuario?.role ?: "")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PerfilUser(
    usuario: Usuario,
    onVolver: () -> Unit,
    onLogOut: () -> Unit = {}
) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf(usuario.name) }
    var apellidos by remember { mutableStateOf(usuario.apellidos) }
    var telefono by remember { mutableStateOf(usuario.phone) }
    var direccion by remember { mutableStateOf(usuario.direccion) }
    var codigoPostal by remember { mutableStateOf(usuario.codigoPostal) }
    var localidad by remember { mutableStateOf(usuario.localidad) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Perfil de cliente")

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = codigoPostal,
                onValueChange = { codigoPostal = it },
                label = { Text("Código postal") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = localidad,
                onValueChange = { localidad = it },
                label = { Text("Localidad") },
                modifier = Modifier.fillMaxWidth()
            )

            DatoPerfil("Email", usuario.email)
            DatoPerfil("DNI", usuario.dni)
            DatoPerfil("Rol", usuario.role)
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Button(
        onClick = {
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            if (uid == null) {
                Toast.makeText(context, "Usuario no válido", Toast.LENGTH_SHORT).show()
                return@Button
            }

            val updates = hashMapOf<String, Any>(
                "name" to nombre,
                "apellidos" to apellidos,
                "phone" to telefono,
                "direccion" to direccion,
                "codigoPostal" to codigoPostal,
                "localidad" to localidad
            )

            FirebaseDatabase
                .getInstance("https://repairme-956fd-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("users")
                .child(uid)
                .updateChildren(updates as Map<String, Any>)
                .addOnSuccessListener {
                    Log.d("PROFILE", "Perfil actualizado")
                    Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.d("PROFILE", "Error actualizando perfil: ${e.message}")
                    Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Guardar cambios")
    }
    Button(
        onClick = {
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            if (uid == null) {
                Toast.makeText(context, "Usuario no válido", Toast.LENGTH_SHORT).show()
                return@Button
            }
            FirebaseDatabase
                .getInstance("https://repairme-956fd-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("users")
                .child(uid)
                .updateChildren(mapOf("estado" to "Inactivo") as Map<String, Any>)
                .addOnSuccessListener {
                    Log.d("PROFILE", "Perfil actualizado")
                    Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    onLogOut()//No mover. Si lo sacamos de addOnSuccessListener se nos desloguea antes de cambiar el estado
                }
                .addOnFailureListener { e ->
                    Log.d("PROFILE", "Error actualizando perfil: ${e.message}")
                    Toast.makeText(context, "Error al guardar", Toast.LENGTH_SHORT).show()
                }

        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Dar de baja usuario")
    }

    Button(
        onClick = { onVolver() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Volver")
    }
}

@Composable
fun PerfilTecnico(
    usuario: Usuario,
    onVolver: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Perfil de técnico")
            DatoPerfil("Nombre", usuario.name)
            DatoPerfil("Email", usuario.email)
            DatoPerfil("Rol", usuario.role)
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Button(onClick = { onVolver() }) {
        Text("Volver")
    }
}

@Composable
fun PerfilAdmin(
    usuario: Usuario,
    onVolver: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Perfil de administrador")
            //DatoPerfil("Nombre", usuario.name)
            //DatoPerfil("Apellidos", usuario.apellidos)
            DatoPerfil("Email", usuario.email)
            //DatoPerfil("Teléfono", usuario.phone)
            DatoPerfil("Rol", usuario.role)
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Button(onClick = { onVolver() }) {
        Text("Volver")
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