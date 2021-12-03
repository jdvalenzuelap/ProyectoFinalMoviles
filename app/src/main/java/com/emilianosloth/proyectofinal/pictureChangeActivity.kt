package com.emilianosloth.proyectofinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class pictureChangeActivity : AppCompatActivity() {

    lateinit var newURL : EditText
    lateinit var change : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_change)

        newURL = findViewById(R.id.newURLET)
        change = findViewById(R.id.changePictureURL)

        change.setOnClickListener{
            if(newURL.text.toString() == ""){
                Toast.makeText(this, "Falta un URL", Toast.LENGTH_SHORT).show()
            }else{
                changePicURL()
            }
        }
    }

    fun changePicURL(){
        val user = FirebaseAuth.getInstance().currentUser
        val db = Firebase.firestore

        db.collection("usuarios")
            .whereEqualTo("id", user!!.email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    val id = document.id
                    db.collection("usuarios")
                        .document(id)
                        .update("imageURL", newURL.text.toString())
                }
                finish()
            }
            .addOnFailureListener{
                Log.d("FIREBASE", "EXCEPTION: ${it.message}")
                Toast.makeText(this, "ERROR: COULDN'T LOAD NEW NAME", Toast.LENGTH_SHORT).show()
            }

    }


}

