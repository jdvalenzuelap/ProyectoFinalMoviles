package com.emilianosloth.proyectofinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PasswordChangeActivity : AppCompatActivity() {

    lateinit var newPass1: EditText
    lateinit var newPass2: EditText
    lateinit var acceptChange: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_change)

        newPass1 = findViewById(R.id.newPass1)
        newPass2 = findViewById(R.id.newPass2)
        acceptChange = findViewById(R.id.cpButton)

        acceptChange.setOnClickListener {
            if (newPass1.text.toString() == newPass2.text.toString()){
                if(newPass1.length() >= 6){
                    changePassword();
                }else{
                    Toast.makeText(this, "at least 6 characters", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Passwords dont match", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun changePassword(){
        val user = FirebaseAuth.getInstance().currentUser
        val txtNewPass = newPass1.text.toString()

        user!!.updatePassword(txtNewPass)
            .addOnCompleteListener{
                if (it.isSuccessful){
                    Toast.makeText(this, "password changed", Toast.LENGTH_SHORT).show()
                    finish();
                }else{
                    Log.e("FIREBASE", "password Change Unsuccesful: ${it.exception?.message}")
                }
            }


    }
}