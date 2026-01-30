package com.example.repairme.ui.screens.auth
import android.widget.Space
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repairme.ui.theme.botonNaranja

import com.example.repairme.ui.theme.naranjaLetras


@Composable
fun LoginScreen(){
    var email by remember{ mutableStateOf("") }
    var contrasena by remember{ mutableStateOf("") }

    Column (

        modifier = Modifier.padding(start = 32.dp, end = 32.dp)
            .fillMaxSize()//Ocupa todo el espacio disponible
            .background(Color.LightGray),
            horizontalAlignment = Alignment.CenterHorizontally,
           // verticalArrangement = Arrangement.Center

    ){
        Spacer(modifier = Modifier.height(50.dp))
        Text(text = "Iniciar Sesión",
            style= TextStyle(color = naranjaLetras),
            fontSize = 30.sp,
            modifier = Modifier.padding(top=12.dp, bottom = 32.dp)
        )
        Spacer(modifier = Modifier.height(30.dp))

        TextField(
            //modifier = Modifier.fillMaxSize(),
            textStyle = TextStyle(textAlign = TextAlign.Center),
            value = email,
            onValueChange = {email=it},
            label = {Text("email")}


        )
        //Spacer(modifier = Modifier.height(18.dp))

        Spacer(modifier = Modifier.height(34.dp))
        TextField(
            //modifier = Modifier.fillMaxSize(),
            textStyle = TextStyle(textAlign = TextAlign.Center),
            value = contrasena,
            onValueChange = {contrasena=it},
            label = {Text("email")}


        )
        Spacer(modifier = Modifier.height(150.dp
        ))
        Button(
            onClick = {},
            colors=ButtonDefaults.buttonColors(
                containerColor = botonNaranja,
                contentColor = Color.White
            )
        ) { Text("Entrar") }

        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Regístrate aquí",
            color = naranjaLetras
            //Esto tiene que ser cliclable
        )




    }//Acaba la columna
}


