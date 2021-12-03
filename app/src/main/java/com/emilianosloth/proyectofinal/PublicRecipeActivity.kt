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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.concurrent.Executors

class PublicRecipeActivity : AppCompatActivity() {
    lateinit var recipeName: TextView
    lateinit var authorName: TextView
    lateinit var recipeIngredients: TextView
    lateinit var recipeCategory: TextView
    lateinit var recipeImage: ImageView
    lateinit var rReturnBT: Button
    lateinit var checkProfileBT: Button
    lateinit var recRecipe: String
    lateinit var recAuthor: String
    lateinit var idRecipe: String
    lateinit var recyclerView: RecyclerView
    lateinit var steps_list: List<String>
    lateinit var recipeInstructions: String

    val db = Firebase.firestore
    var storageReference = Firebase.storage.reference

    var defaulturl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/1024px-No_image_available.svg.png"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_recipe)
        recipeName = findViewById(R.id.pRecipeNameTV)
        authorName = findViewById(R.id.pRecipeAuthorTV)
        recipeIngredients = findViewById(R.id.pRecipeIngredientsTV)
        recipeCategory = findViewById(R.id.pCategoryTV)
        recipeImage = findViewById(R.id.pRecipeImageIV)
        rReturnBT = findViewById(R.id.pRecipeCloseBT)
        checkProfileBT = findViewById(R.id.viewPrfBT)
        recRecipe = intent.getStringExtra("name").toString()
        recAuthor = intent.getStringExtra("author").toString()
        recyclerView = findViewById(R.id.pStepsRV)
        displayRecipe()

        checkProfileBT.setOnClickListener{
            var intent = Intent(this, PublicUserActivity::class.java)
            intent.putExtra("Author", recAuthor)
            startActivity(intent)
        }
    }

    fun shareRecipe(view: View?){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out this Recipe at Fast Chef App: \n ${recipeName.text} \n ${recipeInstructions} \n Chef: ${authorName.text}\n Download it here: www.androidStoreLinkIfWeHadOne.com")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    fun displayRecipe(){
        db.collection("recetas")
            .whereEqualTo("Recipe Name", recRecipe)
            .whereEqualTo("Autor", recAuthor)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    recipeName.text = document.getString("Recipe Name").toString()
                    authorName.text = "By: " + document.getString("Autor").toString()
                    recipeIngredients.text = document.getString("Ingredients").toString()
                    recipeInstructions = document.getString("Instructions").toString()
                    recipeCategory.text = document.getString("Category").toString()
                    var imageString = document.getString("Image").toString()
                    leerImagen(document.id)
                    idRecipe = document.id

                    /*
                    if(imageString.substring(imageString.length-4) == ".jpg"){
                        var imageReference = storageReference.child("images/"+imageString)
                        loadImg(recipeImage, "https://firebasestorage.googleapis.com/v0/b/proyectofinalmoviles-e98e6.appspot.com/o/images%2Fmolletes.jpg?alt=media&token=171c0d48-f92d-446f-b50b-7a258411d7a8")
                    }else{
                        loadImg(recipeImage, document.getString("Image").toString())
                    }
                    Log.d("FIRESTORE", "${document.id} ${document.data}")
                     */


                    steps_list = enlist(recipeInstructions, "-")
                    val adapter = StepAdapter(steps_list, imageString)
                    var llm = LinearLayoutManager(this)
                    llm.orientation = LinearLayoutManager.VERTICAL

                    // setup the recycler view
                    recyclerView.layoutManager = llm
                    recyclerView.adapter = adapter

                }
            }
            .addOnFailureListener{
                Log.d("FIREBASE", "EXCEPTION: ${it.message}")
                Toast.makeText(this, "ERROR: COULDN'T LOAD RECIPES", Toast.LENGTH_SHORT).show()
            }
    }

    fun leerImagen(nombreImagen : String){
        val storageReference = FirebaseStorage.getInstance().getReference("images/$nombreImagen")
        val localfile = File.createTempFile("imagenTemporal", "jpg")
        storageReference.getFile(localfile)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                recipeImage.setImageBitmap(bitmap)
                Log.d("FIREBASE RECETA IMAGEN", nombreImagen)
            }
            .addOnFailureListener {
                loadImg(recipeImage, defaulturl)
                Log.e("FIREBASE Platillo", "exception: ${it.message}")
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

    fun enlist(inStr: String, del: String): List<String> {
        return inStr.split(del)
    }

}