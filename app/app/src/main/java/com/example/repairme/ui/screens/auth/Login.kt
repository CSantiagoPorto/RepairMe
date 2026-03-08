package com.example.repairme.ui.screens.auth
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repairme.data.repository.AuthRepository
import com.example.repairme.ui.theme.botonNaranja

import com.example.repairme.ui.theme.naranjaLetras


@Preview(showBackground = true)
@Composable
fun LoginScreen(onNavigateToRegistro:()-> Unit={},
                onNavigateToUserScreen: ()-> Unit={},//Aquí está vacío pero luego en App navigation le vamos a dar destino
                onNavigateToTecnicoScreen:()->Unit={},
                onNavigateToAdminScreen:()->Unit={}
) {

    val repo = AuthRepository()
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val context = LocalContext.current

    fun mensaje(context: Context, mensaje: String){
        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
    }
    fun loguearse(){
        if(email.isEmpty()){
            mensaje(context, "Debe proporcionar una dirección de email")
            return
        }
        if (!email.contains("@")){
            mensaje(context, "Debe proporcionar un email válido")
            return
        }
        if (contrasena.isEmpty()){
            mensaje(context, "Debe introducir la contaseña")
            return
        }
        if(contrasena.length<8){
            mensaje(context,"La contraseña debe tener al menos 8 caracteres")
            return
        }
            mensaje(context,"Voy a validar")
        repo.validarCorreoPassword(
            correo = email,
            contraseña = contrasena,
            validacionOK = {usuario->
                Toast.makeText(context, "Hola ${usuario.name}",Toast.LENGTH_LONG).show()
                when(usuario.role.uppercase()){
                    "USER"-> onNavigateToUserScreen()
                    "ADMIN"-> onNavigateToTecnicoScreen()
                    "TECNICO"-> onNavigateToTecnicoScreen
                }
            },
            validacionError = {noEncontrado->
                Toast.makeText(context, "Algo ha fallado y no se encontró el usuario",Toast.LENGTH_SHORT).show()
            }
        )

    }





//Añadir el Scaffold con el Botton Bar y el ToolBar


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
            visualTransformation = PasswordVisualTransformation(), //Así nos oculta la contraseña,
            textStyle = TextStyle(textAlign = TextAlign.Center),
            value = contrasena,
            onValueChange = {contrasena=it},
            label = {Text("contraseña")}


        )
        Spacer(modifier = Modifier.height(150.dp
        ))
        Button(
            onClick = {
                loguearse()

            },
            colors=ButtonDefaults.buttonColors(
                containerColor = botonNaranja,
                contentColor = Color.White
            )
        ) { Text("Entrar") }

        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Regístrate aquí",
            color = naranjaLetras,
            //Esto tiene que ser cliclable
            modifier = Modifier.clickable {
                //Aquí tenemos que llamar a una función de navegación
                onNavigateToRegistro()
            }
        )

//TODO:
        //verificar que los campos tengan contenido. Si no ---> Mensaje error
        //Hay que enchufar con Firebase y validar
        //Si es correcto lee el rol
        //Cuando haya siguiente pantalla tiene que moverse a ahí



    }//Acaba la columna


}


