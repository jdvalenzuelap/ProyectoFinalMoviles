package com.emilianosloth.proyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class activity_user_profile : AppCompatActivity() {
    lateinit var email : TextView
    lateinit var recipes : TextView
    var contador = 0
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        email = findViewById(R.id.UPEmail)
        recipes = findViewById(R.id.UPNumber_recipes)

        email.text = Firebase.auth.currentUser?.email
        
        db.collection("recetas").get()
            .addOnSuccessListener { 
                for (documento in it){
                    Log.d("FIRESTORE","${documento.id} : ${documento.data["Autor"]}")
                    if (documento.data["Autor"] == Firebase.auth.currentUser?.email){
                        contador++
                    }
                    Log.d("FIRESTORE", "${contador}")
                }
            }
        
            .addOnFailureListener {
                Toast.makeText(this, "No se pudo leer la base de datos", Toast.LENGTH_SHORT).show()
            }

        recipes.text = contador.toString()
        
    }
    
    fun close(view: View?){
        finish()
    }

}