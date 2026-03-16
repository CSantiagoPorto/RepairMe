package com.example.repairme.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repairme.data.model.Averia
import com.example.repairme.ui.theme.Naranja


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AdminScreen(
    onVerAverias: ()->Unit={},
    onVerTecnicos:()-> Unit={},
    onIrPerfil: () -> Unit = {},

    onLogOut: ()->Unit={}
){
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Panel del administrador") },
                actions = {
                    IconButton(onClick = onLogOut) {
                        Icon(Icons.Default.ExitToApp, contentDescription ="salir")
                    }
                }
            )
        }//Cierra topBar

    ){ innerPadding->
        Column ( modifier= Modifier.padding(innerPadding))
        {
            Text("Bienvenido, Admin")
            Card(//Esta va a ser la car de reparaciones
                //Aquí hay que ponerle los modificadores de espacio de la card
                //Y también hay que ponerle para que al expandir muestre las reparaciones
                onClick = {onVerAverias()}
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(text = "Reparaciones",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Naranja
                    )
                    Icon(
                        imageVector = Icons.Filled.Build,
                        contentDescription = "Reparaciones",
                        tint = Naranja,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
           Card (onClick = {onVerTecnicos()}){ Row (modifier = Modifier
               .fillMaxWidth()
               .padding(20.dp),
               horizontalArrangement = Arrangement.SpaceBetween,
               verticalAlignment = Alignment.CenterVertically)
           {
               Text(text = "Técnicos",
                   fontSize = 18.sp,
                   fontWeight = FontWeight.SemiBold,
                   color = Naranja
               )
               Icon(
                   imageVector = Icons.Filled.Engineering,
                   contentDescription = "Técnicos",
                   tint = Naranja,
                   modifier = Modifier.size(40.dp)
               )

           }  }
          Card (){
              Row (modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically)
              {
              Text(text = "Clientes",
                  fontSize = 18.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Naranja
              )
              Icon(
                  imageVector = Icons.Filled.Person,
                  contentDescription = "Clientes",
                  tint = Naranja,
                  modifier = Modifier.size(40.dp)
              )

            }
          }
            Card() {
                Row (modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Text(text = "Presupuestos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Naranja
                    )
                    Icon(
                        imageVector = Icons.Filled.RequestQuote,
                        contentDescription = "Presupuestos",
                        tint = Naranja,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            // Perfil del admin
            Card(onClick = { onIrPerfil() }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mi perfil",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Naranja
                    )
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Mi perfil",
                        tint = Naranja,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun AdminScreenPreview() {
    AdminScreen()
}