package com.emilianosloth.proyectofinal

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executors

class PublicUserActivity : AppCompatActivity() {

    lateinit var recipesBT: Button
    lateinit var followBT: Button
    lateinit var closeBT: Button
    lateinit var profilePic: ImageView
    lateinit var displayName: TextView
    lateinit var displayUser: TextView
    lateinit var authorSTR: String

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_user)
        authorSTR = intent.getStringExtra("Author").toString()
        recipesBT = findViewById(R.id.pViewBT)
        followBT = findViewById(R.id.followBT)
        closeBT = findViewById(R.id.puReturnBT)
        profilePic = findViewById(R.id.pProfileIMG)
        displayName = findViewById(R.id.pNameTV)
        displayUser = findViewById(R.id.pUserMailTV)

        db.collection("usuarios")
            .whereEqualTo("id", authorSTR)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    loadImg(profilePic, document.getString("imageURL").toString())
                    displayName.text = document.getString("name").toString()
                    displayUser.text = document.getString("id").toString()
                }
            }
            .addOnFailureListener{
                Log.e("FIRABASE", "id: ${it.message}")
            }

        recipesBT.setOnClickListener{
            var intent = Intent(this, MyRecipesActivity::class.java)
            intent.putExtra("User", displayUser.getText().toString())
            startActivity(intent)
        }

        closeBT.setOnClickListener{
            finish()
        }
    }

    fun loadImg(view: ImageView, url: String){
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        var image: Bitmap? = null

        // Only for Background process (can take time depending on the Internet speed)
        executor.execute {
            // Tries to get the image and post it in the ImageView
            // with the help of Handler
            try {
                val `in` = java.net.URL(url).openStream()
                image = BitmapFactory.decodeStream(`in`)
                // Only for making changes in UI
                handler.post {
                    view.setImageBitmap(image)
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun follow(view: View?){
        var seguidos : ArrayList<String>
        db.collection("usuarios")
            .whereEqualTo("id", Firebase.auth.currentUser?.email.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    seguidos = document.get("seguidos") as ArrayList<String>
                    if (seguidos.contains("${displayUser.text}")){
                        seguidos.remove("${displayUser.text}")
                    }else{
                        seguidos.add("${displayUser.text}")
                    }

                    db.collection("usuarios").document(document.id).update("seguidos", seguidos)
                }


            }
            .addOnFailureListener{
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}