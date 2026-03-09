package com.example.repairme.data.repository

import com.example.repairme.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class UserRepository : OperationsTemplateRepository() {

    private var autenticacion = FirebaseAuth.getInstance()
    private val NODE = "users"




}