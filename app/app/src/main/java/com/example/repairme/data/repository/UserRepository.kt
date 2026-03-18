package com.example.repairme.data.repository

import com.example.repairme.data.model.Averia
import com.example.repairme.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class UserRepository : OperationsTemplateRepository() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val NODE = "users"
    private val NODE2="repairs"

    //Aquí dejo esto porque el técnico va a necesitar recuperar los usuarios
    //También buscarlos por nombre

    fun obtenerCualquierUsuarioPorId(
        fallo:(String)->Unit,
        exito:(Usuario)->Unit,
        id:String
    ){
        ref("$NODE/$id").get().addOnSuccessListener {
            snapshot->
            val usuario=snapshot.getValue(Usuario::class.java)
            if(usuario!=null){
                exito(usuario)
            } else{
                fallo("No se encontró ningún usuario con ese id")
            }
        }.addOnFailureListener {
            fallo("Error de bbdd para obtener el usuario")
        }
    }

    fun obtenerUsuariosTodos(
        fallo:(String)->Unit,
        exito:(List<Usuario>)->Unit
    ){
        var listaCliente= mutableListOf<Usuario>()
        val clienteRef= ref(NODE)

        clienteRef.orderByChild("role").equalTo("user")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children){
                        val uidCliente = child.key ?: ""
                        val cliente = child.getValue(Usuario::class.java)

                        if (cliente != null){
                            listaCliente.add(cliente.copy(id = uidCliente))
                            //Aquí hay que ponerle el uid porque no se guarda como documento
                        }
                    }
                    exito(listaCliente)
                }

                override fun onCancelled(error: DatabaseError) {
                    fallo("No se ha podido recuperar la lista de técnicos disponibles")
                }
            })
    }
    fun obtenerAveriasPorUsuario(
        userId: String,
        fallo: (String) -> Unit,
        exito: (List<Averia>) -> Unit
    ) {
        val listaAverías = mutableListOf<Averia>()
        val averiaRef = ref(NODE2)

        averiaRef.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val averia = child.getValue(Averia::class.java)
                        if (averia != null) listaAverías.add(averia)
                    }
                    exito(listaAverías)
                }
                override fun onCancelled(error: DatabaseError) {
                    fallo(error.message)
                }
            })
    }







}