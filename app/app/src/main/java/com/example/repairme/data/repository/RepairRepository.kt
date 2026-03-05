package com.example.repairme.data.repository

import com.example.repairme.data.model.Averia
import com.google.firebase.auth.FirebaseAuth

class RepairRepository : OperationsTemplateRepository() {

    private val auth = FirebaseAuth.getInstance()
    private val NODE = "repairs"

    fun abrirAveria(
        averia: Averia,
        exito:()-> Unit,
        fallo:(String)->Unit

    ){
        val userId= auth.currentUser?.uid
        if (userId==null){
            fallo("Usuario no encontrado")
            return
        }

        val averiaUid= newId(NODE)



        if(averiaUid.isBlank()){
            fallo("Se produjo un error al crear el id de la avería")
            return
        }

        val repair= averia.copy(
            id=averiaUid,
            userId= userId
        )

        setValue("$NODE/$averiaUid", repair,
            ok = { exito() },
            error = { msg -> fallo(msg) }
        )
    }

    // El admin necesita todas las averías para poder asignarlas
    fun obtenerAveriasTodas(
        fallo: (String) -> Unit,
        exito: (List<Averia>) -> Unit
    ){
        var listaAverías= mutableListOf<Averia>()
        val averiaRef= ref(NODE)
        //Quiero que me muestre primero las pendientes de asignar, así que hago una lista de estados
        val estado= listOf("Pendiente", "Asignada", "Presupuestada", "En reparación", "reparado")
        averiaRef.orderByChild("estado").get().addOnSuccessListener {
            snashot->
            for (child in snashot.children){
                val averia= child.getValue(Averia::class.java)
                if(averia!=null){
                    listaAverías.add(averia)
                }
            }
            exito(listaAverías.sortedBy { estado.indexOf(it.estado) })
        }.addOnFailureListener{e->fallo("No se encontraron las averías")}
    }


    fun obtenerAveriaUser(
        fallo: (String) -> Unit,
        exito: (List<Averia>) -> Unit
    ){
        var listaAverías= mutableListOf<Averia>()
        val userId= auth.currentUser?.uid
        val averiaRef= ref(NODE)
        if(userId==null){
            fallo("No se encontró el usuario")
            return
        }

        averiaRef.orderByChild("userId").equalTo(userId).get().addOnSuccessListener {
            snapshot->
            for(child in snapshot.children){
                val averia= child.getValue(Averia::class.java)
                if(averia!=null){
                    listaAverías.add(averia)
                }
            }
            exito(listaAverías)
        }.addOnFailureListener(
            {e->fallo("No se encontrarion averías")}
        )
    }


    //Editar avería completa
    fun editarAveria(
        averiaEditada:Averia,
        exito:()->Unit,
        fallo:(String)->Unit
    ){
        if(averiaEditada.id.isBlank()){
            fallo("La avería no tiene id")
            return
        }


        setValue("$NODE/${averiaEditada.id}", averiaEditada,
            ok = { exito() },
            error = { msg-> fallo(msg) }
        )
    }

    //Eliminar avería
    fun eliminarAveria(
        averiaId:String,
        exito:()->Unit,
        fallo:(String)->Unit
    ){
        if(averiaId.isBlank()){
            fallo("La avería no tiene id")
            return
        }

        delete("$NODE/$averiaId",
            ok = { exito() },
            error = { msg-> fallo(msg) }
        )
    }

}