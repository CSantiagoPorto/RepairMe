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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AdminScreen() {
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
            "repair" -> RepairListScreen(orangePrimary) { currentScreen = null }
            "repaired" -> RepairedListScreen(orangePrimary) { currentScreen = null }
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
                onClick = { }
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
    icon: ImageVector, // Revisar
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
fun RepairListScreen(orangePrimary: Color, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
            items(10) { index ->
                RepairItem("Equipo ${index + 1}")
            }
        }
    }
}

@Composable
fun RepairedListScreen(orangePrimary: Color, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
            items(10) { index ->
                RepairItem("Equipo reparado ${index + 1}")
            }
        }
    }
}

@Composable
fun RepairItem(title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp), // Añadir background redondeado y elevation 2.dp?
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(16.dp),
            fontSize = 14.sp
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun AdminScreenPreview() {
    AdminScreen()
}
