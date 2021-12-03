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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executors

class ProfileActivity : AppCompatActivity() {

    lateinit var upBT: Button
    lateinit var changeNameBT: Button
    lateinit var viewBT: Button
    lateinit var preturnBT: Button
    lateinit var changePass: Button
    lateinit var profilePic: ImageView
    lateinit var displayName: TextView
    lateinit var displayUser: TextView
    lateinit var changePicButton: Button

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        upBT = findViewById(R.id.upBT)
        changeNameBT = findViewById(R.id.pChangeNameBT)
        viewBT = findViewById(R.id.viewBT)
        preturnBT = findViewById(R.id.pReturnBT)
        changePass = findViewById(R.id.pChangePass)
        profilePic = findViewById(R.id.profileImage)
        displayName = findViewById(R.id.nameTV)
        displayUser = findViewById(R.id.userMailTV)
        changePicButton = findViewById(R.id.changePicBT)

        var url = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/1024px-No_image_available.svg.png"
        var name = "default"

        db.collection("usuarios")
            .whereEqualTo("id", Firebase.auth.currentUser?.email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    loadImg(profilePic, document.getString("imageURL").toString())
                    displayName.text = document.getString("name").toString()
                    displayUser.text = document.getString("id").toString()
                    Log.d("FIRABASE", "id: ${name}")
                    Log.d("FIRABASE", "id: ${url}")
                }
            }
            .addOnFailureListener{
                Log.e("FIRABASE", "id: ${it.message}")
            }

        upBT.setOnClickListener {
            var intent = Intent(this, CreateRecipeActivity::class.java)
            startActivity(intent)
        }

        viewBT.setOnClickListener {
            var intent = Intent(this, MyRecipesActivity::class.java)
            intent.putExtra("User", displayUser.getText().toString())
            startActivity(intent)
        }

        preturnBT.setOnClickListener {
            finish()
        }

        changePass.setOnClickListener {
            var intent = Intent(this, PasswordChangeActivity::class.java)
            startActivity(intent)
        }

        changeNameBT.setOnClickListener{
            var intent = Intent(this, NameChangeActivity::class.java)
            startActivity(intent)
        }

        changePicButton.setOnClickListener{
            var intent = Intent(this, pictureChangeActivity::class.java)
            startActivity(intent)
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

    fun goBack(view: View?){
        finish()
    }

    fun viewLikes(view: View?){
        intent = Intent(this, UserLikeActivity::class.java)
        startActivity(intent)
    }

    fun viewFollowing(view: View?){
        intent = Intent(this, FollowingActivity::class.java)
        startActivity(intent)
    }

}