package com.example.repairme.data.model

enum class EstadoAveria {
    Pendiente, PendienteReasignar, Asignada, PendienteMaterial, Presupuestada, EnReparacion,Reparado, ListaParaRecoger, Declinada
}

//Estos son los estados posibles de una reparación
//El flujo habitual va a ser Pendiente, Asignada, Presupuestada, enReparacion, Reparado, ListaParaRecoger