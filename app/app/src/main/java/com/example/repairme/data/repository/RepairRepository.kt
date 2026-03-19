package com.example.repairme.data.repository

import android.util.Log
import android.util.Log.e
import com.example.repairme.data.model.Averia
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class RepairRepository : OperationsTemplateRepository() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val NODE = "repairs"

    fun abrirAveria(
        averia: Averia,
        exito: () -> Unit,
        fallo: (String) -> Unit

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
        val listaAverías= mutableListOf<Averia>()
        val averiaRef= ref(NODE)
        //Quiero que me muestre primero las pendientes de asignar, así que hago una lista de estados
        val estado= listOf("Pendiente", "Asignada", "Presupuestada", "En reparación", "reparado")
        averiaRef.orderByChild("estado").get().addOnSuccessListener {
            snapshot->
            for (child in snapshot.children){
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
        val listaAverías= mutableListOf<Averia>()
        val userId= auth.currentUser?.uid
        val averiaRef= ref(NODE)
        Log.d("TESTCRUD", "obtenerAveriaUser llamado, userId=$userId")
        if(userId==null){
            fallo("No se encontró el usuario")
            return
        }

        averiaRef.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("TESTCRUD", "onDataChange, hijos=${snapshot.childrenCount}")
                   for(child in snapshot.children){
                       val averia= child.getValue(Averia::class.java)
                       if(averia!=null){
                           listaAverías.add(averia)
                       }
                   }
                    Log.d("TESTCRUD", "lista final: ${listaAverías.size}")
                    exito(listaAverías)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d("TESTCRUD", "onCancelled: ${error.message}")
                    fallo(error.message)
                }
            })

    }
    fun obtenerAveriaId(
        fallo: (String) -> Unit,
        exito: (Averia) -> Unit,
        averiaId:String
    ){


        val averiaRef= ref("$NODE/$averiaId")

       averiaRef.get().addOnSuccessListener {
           snapshot ->
           val averia= snapshot.getValue(Averia::class.java)
           if(averia!=null){exito(averia)}
           else fallo("Avería no encontrada")
       }.addOnFailureListener {
           e->fallo("Algo pasó ")
       }

    }

    // Obtener Averia tecnico en tiempo real
    fun obtenerAveriasTecnico(
        fallo: (String) -> Unit,
        exito: (List<Averia>) -> Unit
    ) {
        val tecnicoId = auth.currentUser?.uid
        val averiaRef= ref(NODE)

        if (tecnicoId == null) {
            fallo("No se encontró el técnico")
            return
        }

        // Pongo addValueEventListener para que me devuelva los cambios en tiempo real
        averiaRef.orderByChild("tecnicoId").equalTo(tecnicoId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listaAverias= mutableListOf<Averia>()
                    // Usamos el for para recorrer los hijos
                    for (child in snapshot.children) {
                        val averia = child.getValue(Averia::class.java)
                        if (averia != null) {
                            listaAverias.add(averia)
                        }
                    }
                    exito(listaAverias)
                }

                override fun onCancelled(error: DatabaseError) {
                    fallo(error.message)
                }
            })
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
