package com.example.repairme.ui.screens.auth

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

// Colores
import com.example.repairme.ui.theme.Naranja
import com.example.repairme.ui.theme.GrisFondoPantalla

// Para previsualizar la pantalla en design
@Preview(showBackground = true)

@Composable
fun RegisterScreen(
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
    var direccion by rememberSaveable { mutableStateOf("") }
    var codigoPostal by rememberSaveable { mutableStateOf("") }
    var localidad by rememberSaveable { mutableStateOf("") }
    var dni by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    //var password by rememberSaveable { mutableStateOf("") }


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

        if (pass.trim().length < 6) {
            error = "Contraseña debe tener al menos 6 caracteres"
            return false
        }
        if (
            nombre.trim().isEmpty() ||
            apellidos.trim().isEmpty() ||
            direccion.trim().isEmpty() ||
            codigoPostal.trim().isEmpty() ||
            localidad.trim().isEmpty() ||
            dni.trim().isEmpty() ||
            telefono.trim().isEmpty()
        ) {
            error = "Rellena todos los campos"
            return false
        }

        // Código Postal con 5 números
        if (!Regex("^\\d{5}$").matches(codigoPostal.trim())) {
            error = "Código postal inválido (deben ser 5 números)"
            return false
        }

        // DNI/NIF con 8 números y una letra
        val dniMayus = dni.trim().uppercase()
        if (!Regex("^\\d{8}[A-Z]$").matches(dniMayus)) {
            error = "DNI/NIF inválido (8 números y letra)"
            return false
        }

        // Teléfono con 9 números
        if (!Regex("^\\d{9}$").matches(telefono.trim())) {
            error = "Teléfono inválido (deben ser 9 números)"
            return false
        }

        // Contraseña mínimo 8 caracteres
        if (pass.length < 8) {
            error = "La contraseña debe tener mínimo 8 caracteres"
            return false
        }


        // Si todo va bien, limpiamos error y dejamos el DNI en mayúsculas
        dni = dniMayus
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
            text = "Registro",
            style = MaterialTheme.typography.headlineMedium,
            color = Naranja,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
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

        // Campos del formulario
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

        // Campo DNI/NIF (lo ponemos en mayúsculas mientras escribe)
        OutlinedTextField(
            value = dni,
            onValueChange = {
                dni = it.uppercase()
                ok = false
                error = null
            },
            label = { Text("DNI/NIF") },
            modifier = Modifier.fillMaxWidth()
        )
        // Campo Teléfono (solo números y máximo 9)
        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = it.filter { c -> c.isDigit() }.take(9)
                ok = false
                error = null
            },
            label = { Text("Teléfono") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        // Direccion
        OutlinedTextField(
            value = direccion,
            onValueChange = {
                direccion = it
                error = null
            },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Código Postal (solo números y máximo 5)
        OutlinedTextField(
            value = codigoPostal,
            onValueChange = {
                // Filtramos para que solo se escriban números
                codigoPostal = it.filter { c -> c.isDigit() }.take(5)
                error = null
            },
            label = { Text("Código Postal") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Campo Localidad
        OutlinedTextField(
            value = localidad,
            onValueChange = {
                localidad = it
                error = null
            },
            label = { Text("Localidad") },
            modifier = Modifier.fillMaxWidth()
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
                        telefono = telefono.trim(),
                        direccion = direccion.trim(),
                        codigoPostal = codigoPostal.trim(),
                        localidad = localidad.trim(),
                        dni = dni.trim().uppercase(),
                        creadoOK = {
                            ok = false
                            Toast.makeText(context, "Usuario creado", Toast.LENGTH_LONG).show()
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
            Text("Registrarme")
        }

        TextButton(onClick = onNavigateBack) {
            Text("¿Ya tienes cuenta? Inicia sesión", color = Naranja)
        }
    }
}

@Composable
fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreen()
    }
}