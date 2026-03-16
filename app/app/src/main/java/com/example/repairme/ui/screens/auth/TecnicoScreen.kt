package com.example.repairme.ui.screens.auth
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.repairme.data.model.Averia
import com.example.repairme.data.repository.RepairRepository
import androidx.compose.foundation.lazy.items


@Composable
fun TecnicoScreen(
    onAddEquipo: () -> Unit = {},
    onAveriaClick: (String) -> Unit={},
    onIrPerfil: () -> Unit = {}

) {
    val orangePrimary = Color(0xFFE67E22)
    val grayBackground = Color(0xFFF5F5F5)

    var currentScreen by remember { mutableStateOf<String?>(null) }




    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(grayBackground)) {
        // Top Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ClearRepair",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = orangePrimary
            )
        }

        // Main Content
        when (currentScreen) {
            "repair" -> RepairListScreen(orangePrimary= orangePrimary, onBack =  { currentScreen = null }, onAveriaClick=onAveriaClick)
            "repaired" -> RepairedListScreen(orangePrimary=orangePrimary, onBack =  { currentScreen = null }, onAveriaClick={})
            else -> HomeContent(orangePrimary) { screen -> currentScreen = screen }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavButton(
                icon = Icons.Filled.Build,
                label = "Reparar",
                color = orangePrimary,
                onClick = { currentScreen = "repair" }
            )
            BottomNavButton(
                icon = Icons.Filled.Computer,
                label = "Reparados",
                color = orangePrimary,
                onClick = { currentScreen = "repaired" }
            )
            BottomNavButton(
                icon = Icons.Filled.Person,
                label = "Perfil",
                color = orangePrimary,
                onClick = { onIrPerfil() }
            )
        }
    }
}

@Composable
fun HomeContent(orangePrimary: Color, onCardClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Card 1: Equipos en reparación
        CardItem(
            title = "Equipos en\nreparación",
            icon = Icons.Filled.Build,
            accentColor = orangePrimary,
            onClick = { onCardClick("repair") }
        )

        // Card 2: Equipos reparados
        CardItem(
            title = "Equipos\nreparados",
            icon = Icons.Filled.Computer, //cambiar el icono de este
            accentColor = orangePrimary,
            onClick = { onCardClick("repaired") }
        )
    }
}

@Composable
fun CardItem(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp), // Añadir background redondeado y elevation 4,dp?

    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = accentColor
            )
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun BottomNavButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = color
        )
    }
}

@Composable
fun RepairListScreen(orangePrimary: Color, onBack: () -> Unit,onAveriaClick: (String) -> Unit) {
    val equipmentStates = remember { mutableStateOf(List(10) { "Esperando confirmación" }) }
    var listaAverias by remember{mutableStateOf(listOf<Averia>()) }
    var repo= remember { RepairRepository() }
    LaunchedEffect(Unit) {
        repo.obtenerAveriasTecnico(
            fallo = {},
            exito = {averias->listaAverias=averias}
            //La lambda me trae el dato de fb,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = orangePrimary
            )
        ) {
            Text("← Volver")
        }
        Text(
            text = "Equipos en reparación",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = orangePrimary,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listaAverias) { averia ->
                RepairItem(
                    title = "Equipo ${averia.equipoNombre}",
                    currentState = averia.estado,
                    orangePrimary = orangePrimary,
                    onStateChange = { newState ->
                        // 1. Creamos el objeto actualizado
                        val averiaActualizada = averia.copy(
                            estado = newState,
                            fechaEntrega = if(newState=="Reparado") System.currentTimeMillis()else averia.fechaEntrega)
                        // 2. Lo enviamos a firebase
                        repo.editarAveria(
                            averiaEditada = averiaActualizada,
                            exito = {/* Se refresca solo por el listener*/},
                            fallo = {}
                        )
                    },
                    onAveriaClick = {onAveriaClick(averia.id)}
                )
            }
        }
    }
}

@Composable
fun RepairedListScreen(orangePrimary: Color, onBack: () -> Unit, onAveriaClick:(String)-> Unit) {
    var listaReparadas by remember { mutableStateOf(listOf<Averia>()) }
    val repo = remember { RepairRepository() }

    LaunchedEffect(Unit) {
        repo.obtenerAveriasTecnico(
            fallo = {},
            exito = { averias ->
                listaReparadas = averias.filter { it.estado == "Reparado" }
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = orangePrimary
            )
        ) {
            Text("← Volver")
        }
        Text(
            text = "Equipos reparados",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = orangePrimary,
            modifier = Modifier.padding(vertical = 12.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            //Cmambio esto para que las lea desde la bbdd
            items(listaReparadas) { averia ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onAveriaClick("ID_$averia") },
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = "${averia.equipoNombre} ---  ${averia.tituloAveria}",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RepairItem(
    title: String,
    currentState: String = "Esperando confirmación",
    orangePrimary: Color = Color(0xFFE67E22),
    onStateChange: (String) -> Unit = {},
    onAveriaClick: () -> Unit={}
) {
    var expanded by remember { mutableStateOf(false) }
    val states = listOf(
        "Esperando confirmación",
        "En reparación",
        "Reparado"
    )
    // El boton cambia a verde si el estado es reparado
    val buttonColor = if (currentState == "Reparado") {
        Color(0xFF4CAF50) // Verde
    } else {
        orangePrimary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = {onAveriaClick()}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )

            Box {
                Button(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = currentState,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    states.forEach { state ->
                        DropdownMenuItem(
                            text = { Text(state) },
                            onClick = {
                                // Aqui llamamos a la funcion que actualiza el Firebase
                                onStateChange(state)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TecnicoScreenPreview(
    onAddEquipo: () -> Unit = {},

    onAveriaClick: (String) -> Unit = {}

) {
    TecnicoScreen()
}
