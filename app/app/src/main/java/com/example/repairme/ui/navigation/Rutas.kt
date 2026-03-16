package com.example.repairme.ui.navigation

sealed class Rutas (val ruta:String) {
    //Equivale a un enum
    data object LOGIN : Rutas("login")
    data object REGISTRO : Rutas("registro")
    data object USERSCREEN : Rutas("pantallaUser")
    data object ADD_EQUIPO : Rutas("addEquipo")
    data object ADMINSCREEN : Rutas("pantallaAdmin")
    data object REGISTRO_TECNICO : Rutas("registroTecnico")
    data object TECNICOSCREEN : Rutas("pantallaTecnico")
    data object TESTCRUD : Rutas("testCrud")
    data object REPAIRSSCREEN : Rutas("VerReparacionesadmin")

    data object DETALLE_AVERIA_TECNICO: Rutas("detalleAveriaTecnico/{averiaId}")

    data object PROFILE : Rutas("profile")

    data object SERVICES : Rutas("services")
}


    //Aquí le estoy diciendo que Login pues es un tipo de ruta
    //con el data object le hacemos un singleton a la instancia de la pantalla y tenemos
    //igual el toString y el equals
