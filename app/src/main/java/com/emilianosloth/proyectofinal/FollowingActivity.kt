package com.emilianosloth.proyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TableRow
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FollowingActivity : AppCompatActivity(), View.OnClickListener {
    val db = Firebase.firestore
    lateinit var seguidosEmail : ArrayList<String>
    lateinit var seguidosImagenes : ArrayList<String>
    lateinit var seguidosVacio : ArrayList<String>
    lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following)

        seguidosEmail = ArrayList()
        seguidosImagenes = ArrayList()
        seguidosVacio = ArrayList()

        recyclerView = findViewById(R.id.seguidosRecyclerView)

        db.collection("usuarios")
            .whereEqualTo("id", Firebase.auth.currentUser?.email)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    seguidosEmail = document.get("seguidos") as ArrayList<String>
                    Log.wtf("seguidos", seguidosEmail.toString())
                }

                db.collection("usuarios")
                    .whereIn("id", seguidosEmail)
                    .get()
                    .addOnSuccessListener {documentos ->
                        for (documento in documentos){
                            seguidosImagenes.add(documento.getString("imageURL").toString())
                            seguidosVacio.add("")
                        }

                        val adapter = RecipeAdapter(seguidosEmail, seguidosVacio, seguidosImagenes, this)
                        val llm = LinearLayoutManager(this)
                        llm.orientation = LinearLayoutManager.VERTICAL

                        recyclerView.layoutManager = llm
                        recyclerView.adapter = adapter


                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                    }

            }
            .addOnFailureListener{
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

    fun goBack(view:View?){
        finish()
    }

    override fun onClick(row: View) {
        var intent = Intent(this, PublicUserActivity::class.java)
        val position = recyclerView.getChildLayoutPosition(row)
        intent.putExtra("Author", seguidosEmail[position])
        startActivity(intent)

    }
}