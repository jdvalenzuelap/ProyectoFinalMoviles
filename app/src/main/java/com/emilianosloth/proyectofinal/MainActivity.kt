package com.emilianosloth.proyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.math.sign
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.ktx.oAuthCredential
import java.util.*
import com.google.android.gms.common.SignInButton

class MainActivity : AppCompatActivity() {
    lateinit var createBT: Button
    lateinit var loginBT: Button
    lateinit var emailET: EditText
    lateinit var passET: EditText
    lateinit var googleLogBT : SignInButton
    lateinit var facebookLogBT :LoginButton
    private val RC_SIGN_IN = 89

    private lateinit var googleSignInClient: GoogleSignInClient
    private val callbackManager = CallbackManager.Factory.create();

    private val EMAIL = "email"

    private var TAG = "Fire89"




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createBT = findViewById(R.id.mainCreateBT)
        loginBT = findViewById(R.id.mainLoginBT)
        emailET = findViewById(R.id.mainMailBT)
        passET  = findViewById(R.id.mainPassET)
        googleLogBT = findViewById(R.id.googleLoginBT)
        facebookLogBT = findViewById(R.id.facebookLoginBT)

        facebookLogBT.setReadPermissions(Arrays.asList(EMAIL))

        val accessToken = AccessToken.getCurrentAccessToken();
        val isLoggedIn = (accessToken != null && accessToken.isExpired)

        createBT.setOnClickListener {
            var intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }

        googleLogBT.setOnClickListener{
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("877326768171-rvj9pe7pjfken2pv53bbh4jg9bm80hod.apps.googleusercontent.com")
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)

            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        facebookLogBT.setOnClickListener{
            facebookLogBT.setReadPermissions("email", "public_profile")
            facebookLogBT.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d("Fire89", "facebook:onSuccess:$loginResult")
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.d("Fire89", "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d("Fire89", "facebook:onError", error)
                }

            })
        }


    }

    fun signInGoogle(view: View?){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    //registrar usuario en firebase
    fun registro(view: View?){
        Firebase.auth.createUserWithEmailAndPassword(
            emailET.text.toString(),
            passET.text.toString()).addOnCompleteListener(this){
                if (it.isSuccessful){
                    Log.d("FIREBASE", "Registro Exitoso")
                }else{
                    Log.e("FIREBASE", "Registro fracaso: ${it.exception?.message}")
                }
        }
    }


    fun login(view: View?){
        if (emailET.text.toString() == "" || passET.text.toString() == ""){
            Toast.makeText(this, "Fields must be filled", Toast.LENGTH_SHORT).show()
            return
        }
        Firebase.auth.signInWithEmailAndPassword(
            emailET.text.toString(),
            passET.text.toString()).addOnCompleteListener(this){
                if (it.isSuccessful){
                    Log.d("FIREBASE", "Login Exitoso")
                    var intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this, "Incorrect User or Password", Toast.LENGTH_SHORT).show()
                }
        }
        emailET.text.clear()
        passET.text.clear()
    }

    fun verificarUsuario(){ //verificar si el ususario  sigue logeaado
        if(Firebase.auth.currentUser == null){
           Toast.makeText(this, "SIN USUARIO", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, Firebase.auth.currentUser?.email, Toast.LENGTH_SHORT).show()
        }
    }

    fun verificarUsuarioGUI(view: View?){
        verificarUsuario()
    }

    override fun onStart(){
        Firebase.auth.signOut()
        super.onStart()
        //verificarUsuario()
        //verificar siempre que la actividad vuelva a correr
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Error90", "Google sign in failed", e)
            }

        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val auth = FirebaseAuth.getInstance()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Fire89", "signInWithCredential:success")
                    val user = auth.currentUser
                    Log.w("Fire89", "firebaseAuthWithGoogle: ${user?.displayName}")
                    var intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Fire89", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        val auth = FirebaseAuth.getInstance()
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    var intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }

}