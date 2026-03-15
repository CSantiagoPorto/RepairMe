package com.example.repairme.data.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

open class OperationsTemplateRepository {

    // Conexión a la bbdd usando lazy para evitar errores en Previews
    private val database by lazy {
        FirebaseDatabase.getInstance(
            "https://repairme-956fd-default-rtdb.europe-west1.firebasedatabase.app"
        )
    }

    // Acceso a una ruta concreta de la bbdd
    protected fun ref(path: String): DatabaseReference = database.getReference(path)

    // Crea o sobrescribe el objeto/nodo completo
    protected fun setValue(
        path: String,
        value: Any,
        ok: () -> Unit,
        error: (String) -> Unit
    ) {
        ref(path).setValue(value)
            .addOnSuccessListener { ok() }
            .addOnFailureListener { e -> error(e.message ?: "Error al editar") }
    }

    // Edita algunos campos
    protected fun updateChildren(
        path: String,
        updates: Map<String, Any?>,
        ok: () -> Unit,
        error: (String) -> Unit
    ) {
        ref(path).updateChildren(updates)
            .addOnSuccessListener { ok() }
            .addOnFailureListener { e -> error(e.message ?: "Error desconocido") }
    }

    // Borra el objeto
    protected fun delete(
        path: String,
        ok: () -> Unit,
        error: (String) -> Unit
    ) {
        ref(path).removeValue()
            .addOnSuccessListener { ok() }
            .addOnFailureListener { e -> error(e.message ?: "Error desconocido") }
    }

    // Crea un ID único
    protected fun newId(path: String): String =
        ref(path).push().key ?: System.currentTimeMillis().toString()
}
