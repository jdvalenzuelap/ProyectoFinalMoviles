package com.emilianosloth.proyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner

class SearchActivity : AppCompatActivity() {
    lateinit var searchBT: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchBT = findViewById(R.id.srchBT)

        //Spinner|Lista desplegable
        val languages = resources.getStringArray(R.array.Categories)
        var sp: String
        //access spinner
        val spinner = findViewById<Spinner>(R.id.srchSp)
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, languages)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    sp = languages[position]
                    spinner.setTag(sp).toString()
                    Log.i("Pos", sp)
                    Log.i("Sp TAG", spinner.getTag().toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        searchBT.setOnClickListener{
            var intent = Intent(this, SearchRecipes::class.java)
            intent.putExtra("Cat", spinner.getTag().toString())
            startActivity(intent)
        }
    }

    fun goBack(view: View?){
        finish()
    }
}