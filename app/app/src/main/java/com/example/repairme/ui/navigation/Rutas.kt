package com.example.repairme.ui.navigation

sealed class Rutas (val ruta:String){//Equivale a un enum
    data object  LOGIN: Rutas("login")
    data object  REGISTRO: Rutas("registro")
    data object  USERSCREEN: Rutas("pantalla user")

    //Aquí le estoy diciendo que Login pues es un tipo de ruta
    //con el data object le hacemos un singleton a la instancia de la pantalla y tenemos
    //igual el toString y el equals
}