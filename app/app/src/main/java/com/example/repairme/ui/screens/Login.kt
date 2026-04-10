package com.example.repairme.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repairme.R
import com.example.repairme.data.repository.AuthRepository
import com.example.repairme.ui.theme.GrisFondoPantalla
import com.example.repairme.ui.theme.botonNaranja
import com.example.repairme.ui.theme.naranjaLetras

@Preview(showBackground = true)
@Composable
fun LoginScreen(
    onNavigateToRegistro:()-> Unit={},
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
                    "ADMIN"-> onNavigateToAdminScreen()
                    "TECNICO"-> onNavigateToTecnicoScreen()
                }
            },
            validacionError = {mensaje->
                Toast.makeText(context, mensaje,Toast.LENGTH_SHORT).show()
            }
        )

    }

    //Añadir el Scaffold con el Botton Bar y el ToolBar
    Scaffold(
        containerColor = GrisFondoPantalla
    ) { innerPadding ->

        Column (
            modifier = Modifier
                .fillMaxSize()//Ocupa todo el espacio disponible
                .background(GrisFondoPantalla)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            // Parte con Logo de la app
            Spacer(modifier = Modifier.height(20.dp))

            // logo de la app
            Image(
                painter = painterResource(id = R.drawable.clear_repair_principal),
                contentDescription = "Logo ClearRepair",
                modifier = Modifier.height(140.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // card para el formulario
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Iniciar Sesión",
                        style= TextStyle(color = naranjaLetras),
                        fontSize = 30.sp,
                        modifier = Modifier.padding(top=12.dp, bottom = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        textStyle = TextStyle(textAlign = TextAlign.Center),
                        value = email,
                        onValueChange = {email=it},
                        label = {Text("email")},
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        visualTransformation = PasswordVisualTransformation(), //Así nos oculta la contraseña,
                        textStyle = TextStyle(textAlign = TextAlign.Center),
                        value = contrasena,
                        onValueChange = {contrasena=it},
                        label = {Text("contraseña")},
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    Button(
                        onClick = {
                            loguearse()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors=ButtonDefaults.buttonColors(
                            containerColor = botonNaranja,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Entrar",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Regístrate aquí",
                color = naranjaLetras,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                //Esto tiene que ser cliclable
                modifier = Modifier.clickable {
                    //Aquí tenemos que llamar a una función de navegación
                    onNavigateToRegistro()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}