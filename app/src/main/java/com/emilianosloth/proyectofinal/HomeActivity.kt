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
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.login.LoginManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.concurrent.Executors

class HomeActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var logoutIB: ImageButton
    lateinit var profileIB: ImageButton
    lateinit var srchIB: ImageButton
    lateinit var recyclerView: RecyclerView
    lateinit var names_data: ArrayList<String>
    lateinit var authors_data: ArrayList<String>
    lateinit var urls_data: ArrayList<String>
    var storageReference = Firebase.storage.reference

    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        logoutIB = findViewById(R.id.logoutIB)
        profileIB = findViewById(R.id.profileIB)
        srchIB = findViewById(R.id.searchIB)
        recyclerView = findViewById(R.id.homerecyclerview)
        names_data = ArrayList()
        authors_data = ArrayList()
        urls_data = ArrayList()

        var totalRecipes = 0
        db.collection("recetas")
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    names_data.add(document.getString("Recipe Name").toString())
                    authors_data.add(document.getString("Autor").toString())
//                    var imageString = document.getString("Image").toString()
//
//                    if(imageString.substring(imageString.length-4) == ".jpg"){
//                        var imageReference = storageReference.child("images/"+imageString)
//                        Log.wtf("IMG", imageReference.toString())
//
//
//                        urls_data.add("https://firebasestorage.googleapis.com/v0/b/proyectofinalmoviles-e98e6.appspot.com/o/images%2Fmolletes.jpg?alt=media&token=171c0d48-f92d-446f-b50b-7a258411d7a8")
//                    }else{
//                        urls_data.add(document.getString("Image").toString())
//                    }
                    urls_data.add(document.id)

                    Log.wtf("Names", names_data[totalRecipes])
                    totalRecipes++

                    val adapter = RecipeAdapter(names_data, authors_data, urls_data,this)
                    var llm = LinearLayoutManager(this)
                    llm.orientation = LinearLayoutManager.VERTICAL

                    // setup the recycler view
                    recyclerView.layoutManager = llm
                    recyclerView.adapter = adapter
                    //Log.d("FIRESTORE", "${document.id} ${document.data}")
                }
            }
            .addOnFailureListener{
                Log.d("FIREBASE", "EXCEPTION: ${it.message}")
                Toast.makeText(this, "ERROR: COULDN'T LOAD RECIPES", Toast.LENGTH_SHORT).show()
            }

        //callRecipes-End

        profileIB.setOnClickListener {
            var intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        srchIB.setOnClickListener {
            var intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    fun showRecipe(view: View?){
        if (view?.getTag().toString() != "https://jbarrios.com.ve/images/nofoto.jpg"){
            var intent = Intent(this, RecipeActivity::class.java)
            intent.putExtra("URL", view?.getTag().toString())
            startActivity(intent)
        }
    }

    fun logout(view: View?){
        Firebase.auth.signOut()
        LoginManager.getInstance().logOut();
        finish()
    }

    override fun onClick(row: View) {
        val position = recyclerView.getChildLayoutPosition(row)
        val intent = Intent(this, PublicRecipeActivity::class.java)
        intent.putExtra("author", authors_data[position])
        intent.putExtra("name", names_data[position])
        startActivity(intent)
    }

}