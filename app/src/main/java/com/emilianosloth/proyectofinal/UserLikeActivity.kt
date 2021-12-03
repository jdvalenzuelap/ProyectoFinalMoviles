package com.emilianosloth.proyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserLikeActivity : AppCompatActivity(), View.OnClickListener {
    val db = Firebase.firestore
    lateinit var likes : ArrayList<String>
    lateinit var likesAuthor : ArrayList<String>
    lateinit var likesName : ArrayList<String>
    lateinit var likesURL : ArrayList<String>
    lateinit var docID : ArrayList<String>
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_like)


        likesName = ArrayList()
        likesAuthor = ArrayList()
        likesURL = ArrayList()

        likes = ArrayList()
        docID = ArrayList()
        recyclerView = findViewById(R.id.likeRecyclerView)


        db.collection("usuarios")
            .whereEqualTo("id", Firebase.auth.currentUser?.email.toString())
            .get()
            .addOnSuccessListener { documents ->
                likes = documents.first().get("likes") as ArrayList<String>
                for (x in likes){

                    var temp = x.split(",")

                    likesName.add(temp[0])
                    likesAuthor.add(temp[1])

                }

                db.collection("recetas")
                    .whereIn("Recipe Name", likesName)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents){
                            if (likesAuthor.contains(document.getString("Autor"))){
                                likesURL.add(document.getString("Image") as String)
                                Log.wtf("likes ", "${document.getString("Recipe Name")}, ${document.getString("Autor")}")
                            }
                        }

                        Log.wtf("adapter", likesURL.toString())

                        val adapter = RecipeAdapter(likesName, likesAuthor, likesURL,this)
                        var llm = LinearLayoutManager(this)
                        llm.orientation = LinearLayoutManager.VERTICAL

                        // setup the recycler view
                        recyclerView.layoutManager = llm
                        recyclerView.adapter = adapter

//
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                    }

            }
            .addOnFailureListener{
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }






        //Ya estan los arrayList de: nombres, autores, img
        //Se llaman likesName, likesAuthor, likesURL



//



    }

    fun goBack(view: View?){
        finish()
    }

    override fun onClick(row: View) {
        val position = recyclerView.getChildLayoutPosition(row)
        val intent = Intent(this, PublicRecipeActivity::class.java)
        intent.putExtra("author", likesAuthor[position])
        intent.putExtra("name", likesName[position])
        startActivity(intent)
    }
}