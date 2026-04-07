package com.example.repairme.ui.navigation

sealed class Rutas (val ruta:String) {
    //Equivale a un enum
    data object LOGIN : Rutas("login")
    data object REGISTRO : Rutas("registro")
    data object USERSCREEN : Rutas("pantallaUser")
    data object ADD_EQUIPO : Rutas("addEquipo")
    data object ADMINSCREEN : Rutas("pantallaAdmin")
    data object REGISTRO_TECNICO : Rutas("registroTecnico")
    data object LISTA_TECNICOS : Rutas("listaTecnicos")
    data object TECNICOSCREEN : Rutas("pantallaTecnico")
    data object TESTCRUD : Rutas("testCrud")
    data object REPAIRSSCREEN : Rutas("VerReparacionesadmin")
    data object DETALLE_AVERIA_FINALIZADA: Rutas("detalleReparacionFinalizada/{averiaId}")
    data object DETALLE_AVERIA_TECNICO: Rutas("detalleAveriaTecnico/{averiaId}")

    data object PROFILE : Rutas("profile")

    data object SERVICES : Rutas("services")

    data object SERVICES_ADMIN : Rutas("services_admin")
    data object CLIENTES_ADMIN : Rutas("clientesPantallaAdmin")
    data object DETALLE_PRESUPUESTO : Rutas("detallePresuCliente/{averiaId}")//Esta es la nueva ruta para
    //que ek cliente vea los presupuestos. Si está sin aceptar le sale con botones
    //La clase contiene el botón para generar el pdf. Este archivo cuando funcione lo clonamos para
    //que reutilizar en las facturas

    data object PRESUPUESTOS_ADMIN: Rutas("presupuestosAdmin")
    data object LISTA_PARA_RECOGER_ADMIN : Rutas("listaParaRecogerAdmin")


}


    //Aquí le estoy diciendo que Login pues es un tipo de ruta
    //con el data object le hacemos un singleton a la instancia de la pantalla y tenemos
    //igual el toString y el equals
