package com.emilianosloth.proyectofinal

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class CreateRecipeActivity : AppCompatActivity() {
    lateinit var recipeName : EditText
    lateinit var recipeIngredients : EditText
    lateinit var recipeStep : EditText
    lateinit var rUpBT: Button
    lateinit var idRecipe: String
    var isEditing: Boolean = false
    val db = Firebase.firestore
    val storage = Firebase.storage

    lateinit var buscarImagen: ActivityResultLauncher<String>
    lateinit var imagenUri: Uri
    var imagenEmpty : Boolean = true
    lateinit var imagen: ImageView

    lateinit var steps: String
    lateinit var addBT: Button

    var stepNum : Int = 0
    var photoNum : Int = 0


    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

        //obtener un tumbnail

        val image = result.data?.extras?.get("data") as Bitmap


        val storageReference = storage.reference
        val imageReference = storageReference.child(recipeName.text.toString()+".jpg")
        val imagesReference = storageReference.child("images/"+recipeName.text.toString()+".jpg")

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()


        var uploadTask = imagesReference.putBytes(data)
        uploadTask.addOnFailureListener{
            Log.wtf("Falla", "fallo")
        }.addOnSuccessListener { taskSnapshot ->
            Log.wtf("Success", "Logrado")
        }



    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)

        recipeName = findViewById(R.id.recipeNameET)
        recipeIngredients = findViewById(R.id.recipeIngredientsET)

        recipeStep = findViewById(R.id.recStepTV)
        rUpBT = findViewById(R.id.recipeUploadBT)

        imagen = findViewById(R.id.previewImageView)
        addBT = findViewById(R.id.addStepBT)
        steps = ""

        if (intent.getStringExtra("NAME") != null && intent.getStringExtra("AUTHOR") != null){
            isEditing = true
            idRecipe = intent.getStringExtra("ID").toString()
            db.collection("recetas")
                .whereEqualTo("Recipe Name", intent.getStringExtra("NAME"))
                .whereEqualTo("Autor", intent.getStringExtra("AUTHOR"))
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents){
                        recipeName.setText(document.getString("Recipe Name").toString())
                        recipeIngredients.setText(document.getString("Ingredients").toString())
                        recipeStep.setText(document.getString("Instructions").toString())
                    }
                }
        }

        //Spinner|Lista desplegable
        val languages = resources.getStringArray(R.array.Categories)
        var sp: String
        //access spinner
        val spinner = findViewById<Spinner>(R.id.spinner)
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

        addBT.setOnClickListener{
            if(recipeStep.text.toString() == ""){
                Toast.makeText(this, "Missing Step Instructions", Toast.LENGTH_SHORT).show()
            }else{
                if( (stepNum+1) == photoNum){
                    steps += recipeStep.text.toString() + "-"
                    stepNum += 1
                    Toast.makeText(this, "Step Added", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Missing Step Photo", Toast.LENGTH_SHORT).show()
                }
            }
        }

        rUpBT.setOnClickListener{
            if(recipeName.text.toString() == "" || recipeIngredients.text.toString() == "" ||
                recipeStep.text.toString() == ""){
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }else{
                steps = steps.substring(0, steps.length - 1)
                val receta = hashMapOf(
                    "Autor" to Firebase.auth.currentUser?.email,
                    "Recipe Name" to recipeName.text.toString(),
                    "Ingredients" to enlist(recipeIngredients.text.toString(), ","),
                    "Instructions" to steps,
                    "Image" to recipeName.text.toString()+".jpg",
                    "Category" to spinner.getTag().toString()
                )
                if(isEditing){
                    db.collection("recetas").document(idRecipe).update(receta as Map<String, Any>)
                }else{
                    db.collection("recetas").add(receta)
                        .addOnSuccessListener {
                            registrarImagen(it.id)
                            Log.d("FIREBASE", "ID: ${it.id}")
                            Toast.makeText(this, "Recipe Uploaded Correctly", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Log.d("FIREBASE", "EXCEPTION: ${it.message}")
                            Toast.makeText(this, "ERROR: COULDN'T UPLOAD RECIPE", Toast.LENGTH_SHORT).show()
                        }
                }

                finish()
            }
        }

        buscarImagen = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if(it != null) {
                imagenUri = it
                imagen.setImageURI(imagenUri)
                imagenEmpty = false
            }
        }
    }

    fun registrarImagen(referenciaDocumento: String) {
        val storageReference = FirebaseStorage.getInstance().getReference("images/$referenciaDocumento")
        storageReference.putFile(imagenUri)
            .addOnSuccessListener {
                Log.d("FIREBASE Agregar Platillo", "Correctamente cargado")
            }
            .addOnFailureListener {
                Log.e("FIREBASE Agregar Platillo", "exception: ${it.message}")
            }
    }

    fun seleccionarImagen(v: View) {
        buscarImagen.launch("image/*")
    }

    fun enlist(inStr: String, del: String): String {
        val inlist = inStr.split(del)
        return inlist.joinToString(separator = "\n")
    }

    fun takePicture(view: View?){
        if((photoNum+1) > (stepNum+1)){
            Toast.makeText(this, "Only one photo per step", Toast.LENGTH_SHORT).show()
        }else{
            photoNum += 1
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(takePhotoIntent)
        }
    }


    fun goBack(view: View?){
        finish()
    }

}