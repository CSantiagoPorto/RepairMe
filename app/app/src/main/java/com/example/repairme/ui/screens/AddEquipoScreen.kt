package com.example.repairme.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.Equipo

// Colores
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.Naranja

// Para previsualizar la pantalla en design
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)

@Composable
fun AddEquipoScreen(
    // Callback para pasar el equipo creado (CONECTAR A LA BDD?)
    onGuardar: (Equipo) -> Unit = {},
    // Volver a la pantalla anterior
    onVolver: () -> Unit = {}
) {
    // Variables/inputs del formulario
    var marca by rememberSaveable { mutableStateOf("") }
    var modelo by rememberSaveable { mutableStateOf("") }
    var numeroSerie by rememberSaveable { mutableStateOf("") }

    // Avería opcional, saldrá cuadro de texto cuando se pulse
    var anadirAveria by rememberSaveable { mutableStateOf(false) }
    var descripcionAveria by rememberSaveable { mutableStateOf("") }

    // Estado UI (error/ok)
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var ok by rememberSaveable { mutableStateOf(false) }

    // Función de validación
    fun validarCampos(): Boolean {
        if (
            marca.trim().isEmpty() ||
            modelo.trim().isEmpty() ||
            numeroSerie.trim().isEmpty()
        ) {
            error = "Rellena Marca, Modelo y Nº de serie"
            return false
        }

        // Si se activa Añadir avería, debe tener contenido
        if (anadirAveria && descripcionAveria.trim().isEmpty()) {
            error = "Describe la avería o desmarca 'Añadir avería'"
            return false
        }

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

        // Título
        Text(
            text = "Registrar equipo",
            style = MaterialTheme.typography.headlineMedium,
            color = Naranja,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Marca
        OutlinedTextField(
            value = marca,
            onValueChange = {
                marca = it
                error = null
                ok = false
            },
            label = { Text("Marca") },
            modifier = Modifier.fillMaxWidth()
        )

        // Modelo
        OutlinedTextField(
            value = modelo,
            onValueChange = {
                modelo = it
                error = null
                ok = false
            },
            label = { Text("Modelo") },
            modifier = Modifier.fillMaxWidth()
        )

        // Número de serie (números y letras)
        OutlinedTextField(
            value = numeroSerie,
            onValueChange = {
                numeroSerie = it
                error = null
                ok = false
            },
            label = { Text("Número de serie") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
            modifier = Modifier.fillMaxWidth()
        )

        // Añadir avería opcional (con un switch)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Añadir avería (opcional)")
            Switch(
                checked = anadirAveria,
                onCheckedChange = {
                    anadirAveria = it
                    error = null
                    ok = false
                    if (!it) {
                        descripcionAveria = ""
                    }
                }
            )
        }

        // Si el switch anterior está activado, aparece el cuadro de texto de descripción
        if (anadirAveria) {
            OutlinedTextField(
                value = descripcionAveria,
                onValueChange = {
                    descripcionAveria = it
                    error = null
                    ok = false
                },
                label = { Text("Describe el problema") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                maxLines = 6
            )
        }

        // Mensajes
        if (error != null) {
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
        }

        if (ok) {
            Text("Equipo guardado ✅")
        }

        // Botón Guardar
        Button(
            onClick = {
                if (validarCampos()) {
                    // Si hay avería, creamos una avería inicial (BDD CREARÁ EL ID?)
                    val listaAverias = if (anadirAveria) {
                        listOf(
                            Averia(
                                descripcion = descripcionAveria.trim(),
                                createdAt = System.currentTimeMillis()
                            )
                        )
                    } else {
                        emptyList()
                    }

                    // Crear objeto Equipo con los datos introducidos
                    val equipo = Equipo(
                        marca = marca.trim(),
                        modelo = modelo.trim(),
                        numeroSerie = numeroSerie.trim(),
                        averias = listaAverias
                    )

                    onGuardar(equipo)
                    ok = true
                    error = null
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
            Text("Guardar equipo")
        }

        // Volver
        TextButton(onClick = onVolver) {
            Text("Volver", color = Naranja)
        }
    }

    @Composable
    fun RegisterScreenPreview() {
        MaterialTheme {
            RegisterScreen()
        }
    }
}


