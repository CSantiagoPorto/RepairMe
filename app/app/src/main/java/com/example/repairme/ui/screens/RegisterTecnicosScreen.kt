

package com.example.repairme.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.repairme.data.repository.AuthRepository
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview

// Colores
import com.example.repairme.ui.theme.Naranja
import com.example.repairme.ui.theme.GrisFondoPantalla

// Para previsualizar la pantalla en design


@Composable
fun RegisterTecnicoScreen(
    onNavigateBack: () -> Unit = {},//Con esta función volvemos al login
    onRegisterSucess: () -> Unit = {}
    //Dejo esta función aquí porque la voy a usar para volver al login cuando
    //el registro haya sido exitoso
) {

    //Creo instancia del repo y del context
    val repo = AuthRepository()
    val context = LocalContext.current

    // Variables/inputs del formulario
    var email by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }
    var nombre by rememberSaveable { mutableStateOf("") }
    var apellidos by rememberSaveable { mutableStateOf("") }


    // Variables para decir si OK o error
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var ok by rememberSaveable { mutableStateOf(false) }

    // Función de validación
    fun validarCampos(): Boolean {
        // Si algún campo está vacío -> error
        if (email.trim().isEmpty() || !email.contains("@")) {
            error = "Email inválido"
            return false
        }

        if (pass.trim().length < 8) {
            error = "Contraseña debe tener al menos 8 caracteres"
            return false
        }
        if (
            nombre.trim().isEmpty() ||
            apellidos.trim().isEmpty()
        ) {
            error = "Rellena todos los campos"
            return false
        }

        // Si todo va bien, limpiamos error
        error = null
        return true
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrisFondoPantalla)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Titulo de la pantalla
        Text(
            text = "Registrar Técnico",
            style = MaterialTheme.typography.headlineMedium,
            color = Naranja,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // Nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                error = null
            },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        // Apellidos
        OutlinedTextField(
            value = apellidos,
            onValueChange = {
                apellidos = it
                error = null
            },
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                error = null
            },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            enabled = !ok
        )

        OutlinedTextField(
            value = pass,
            onValueChange = {
                pass = it
                error = null
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            enabled = !ok
        )

        // Mensajes de error/ok
        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (ok) {
            Text("Registro enviado ✅")
        }

        // Botón de registro
        Button(
            onClick = {
                if (validarCampos()) {
                    ok = true
                    repo.crearUsuario(
                        email = email.trim(),
                        password = pass.trim(),
                        nombre = nombre.trim(),
                        apellidos = apellidos.trim(),
                        telefono = "", // Empty for tecnico
                        direccion = "", // Empty for tecnico
                        codigoPostal = "", // Empty for tecnico
                        localidad = "", // Empty for tecnico
                        dni = "", // Empty for tecnico
                        role = "tecnico", // Set role to tecnico
                        creadoOK = {
                            ok = false
                            Toast.makeText(context, "Técnico creado", Toast.LENGTH_LONG).show()
                            onRegisterSucess()
                        },
                        creadoError = { mensaje ->
                            ok = false
                            error = mensaje
                            Toast.makeText(context, "Error: $mensaje", Toast.LENGTH_LONG).show()
                        }
                    )
                } else {
                    ok = false
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Naranja,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar Técnico")
        }
    }
}
@Preview(showBackground = true)
@Composable
fun RegisterTecnicoScreenPreview() {
    MaterialTheme {
        RegisterTecnicoScreen()
    }
}
