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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executors

class SearchRecipes : AppCompatActivity(), View.OnClickListener {
    lateinit var recyclerView: RecyclerView
    lateinit var names_data: ArrayList<String>
    lateinit var authors_data: ArrayList<String>
    lateinit var urls_data: ArrayList<String>
    lateinit var catSTR: String

    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_recipes)
        catSTR = intent.getStringExtra("Cat").toString()
        recyclerView = findViewById(R.id.searchRecyclerView)
        names_data = ArrayList()
        authors_data = ArrayList()
        urls_data = ArrayList()

        //callRecipes-Start
        var totalRecipes = 0
        db.collection("recetas")
            .whereEqualTo("Category", catSTR)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    names_data.add(document.getString("Recipe Name").toString())
                    authors_data.add(document.getString("Autor").toString())
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
    }

    fun showRecipe(view: View?){
        if (view?.getTag().toString() != "https://jbarrios.com.ve/images/nofoto.jpg"){
            var intent = Intent(this, RecipeActivity::class.java)
            intent.putExtra("URL", view?.getTag().toString())
            startActivity(intent)
        }
    }

    fun goBack(view: View?){
        finish()
    }

    override fun onClick(row: View) {
        val position = recyclerView.getChildLayoutPosition(row)
        val intent = Intent(this, RecipeActivity::class.java)
        intent.putExtra("author", authors_data[position])
        intent.putExtra("name", names_data[position])
        startActivity(intent)
    }
}