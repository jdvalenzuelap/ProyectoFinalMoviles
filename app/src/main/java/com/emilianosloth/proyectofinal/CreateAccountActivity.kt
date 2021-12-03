package com.emilianosloth.proyectofinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CreateAccountActivity : AppCompatActivity() {

    lateinit var createButton: Button
    lateinit var returnBT : Button
    lateinit var emailET: EditText
    lateinit var passET: EditText
    lateinit var name: EditText
    lateinit var imageURL: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        createButton = findViewById(R.id.CACreateBT)
        returnBT = findViewById(R.id.returnBT)
        emailET = findViewById(R.id.CAEmailET)
        passET = findViewById(R.id.CAPassET)
        name = findViewById(R.id.nameET)
        imageURL = findViewById(R.id.profilePicURLET)

    }

    fun goBack(view: View?){
        finish()
    }

    fun registro(view: View?){
        if(name.text.toString() == ""){
            Toast.makeText(this, "Falta un nombre", Toast.LENGTH_SHORT).show()
            return
        }
        Firebase.auth.createUserWithEmailAndPassword(
            emailET.text.toString(),
            passET.text.toString()).addOnCompleteListener(this){
            if (it.isSuccessful){
                Toast.makeText(this, "Registro Exitoso", Toast.LENGTH_SHORT).show()
                registrarDatos();
                Log.d("FIREBASE", "Registro Exitoso")
                Thread.sleep(1000)
                finish()
            }else{
                Toast.makeText(this, "Registro fracaso: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                Log.e("FIREBASE", "Registro fracaso: ${it.exception?.message}")
            }
        }
    }


    fun registrarDatos(){
        //1ero - crear un hashmap
        Log.wtf("CREACIOM", "EMPEZAMOS")
        val usuarios = hashMapOf(
            "id" to Firebase.auth.currentUser?.email,
            "name" to name.text.toString(),
            "imageURL" to imageURL.text.toString(),
            "likes" to ArrayList<String>(),
            "seguidos" to ArrayList<String>()
        )
        Log.wtf("creaciom", usuarios.toString())
        //2ndo agregar a firestore
        Log.wtf("CREACIOM", "SEGUIMOS")
        Firebase.firestore.collection("usuarios")
            .add(usuarios)
            .addOnSuccessListener {
                Log.wtf("FIRABASE", "id: ${it.id}")
            }
            .addOnFailureListener{
                Log.wtf("FIRABASE", "id: ${it.message}")
            }
    }

    fun leerDatos(view: View?){
        //1era - query "normal", solicitud de 1 vez
        Firebase.firestore.collection("usuarios")
            .get()
            .addOnSuccessListener {
                //recorrer query snapshot con un for each por cada dato en nuestro collection
                for (documento in it){
                    Log.e("FIRESTORE", "${documento.id} ${documento.data}")
                }
            }
            .addOnFailureListener{
                Log.e("FIRESTORE", "${it.message}")
            }

        //2da - updates en tiempo real
        /*
        Firebase.firestore.collection("usuarios")
            .addSnapshotListener{ datos, e ->
                //si hay error terminar ejecucion
                if (e != null){
                    Log.e("FIRESTORE", "error: $e")
                    return@addSnapshotListener
                }

                //recorrer el snapshot

                for (cambio in datos!!.documentChanges){
                    when(cambio.type){
                        DocumentChange.Type.ADDED -> Log.d("FIRESTORE", "added ${cambio.document.data}")
                        DocumentChange.Type.MODIFIED -> Log.d("FIRESTORE", "modified: ${cambio.document.data}")
                        DocumentChange.Type.REMOVED -> Log.d("FIRESTORE", "remove: ${cambio.document.data}")
                    }
                }
            }
         */
    }

}