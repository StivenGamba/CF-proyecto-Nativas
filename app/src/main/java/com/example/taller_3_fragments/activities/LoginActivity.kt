package com.example.taller_3_fragments.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taller_3_fragments.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class LoginActivity : AppCompatActivity() {

    private lateinit var btnGoogle: Button
    private lateinit var mGoogleSignClient: GoogleSignInClient
    private val RC_SIGN_IN = 2554
    private val TAG = "GoogleSignIn"


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //configurar google sign in
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()

        //Crear cliente de google sign in
        mGoogleSignClient = GoogleSignIn.getClient(this, gso)

        btnGoogle = findViewById(R.id.buttonGoogleLogin)

        btnGoogle.setOnClickListener {
            singIn()
        }


    }

    private fun singIn() {
        val signIntent: Intent = mGoogleSignClient.signInIntent
        startActivityForResult(signIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(Exception::class.java)

            //inicio de sesion existoso
            Log.d(TAG, "Inicio de sesion exitoso: ${account?.email}")
            Toast.makeText(this, "Bienvenido ${account?.displayName}", Toast.LENGTH_SHORT).show()

            //ir a la pantalla principal
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", account?.email)
            intent.putExtra("name", account?.displayName)
            intent.putExtra("photoUrl", account?.photoUrl.toString())
            startActivity(intent)
            finish()
        }catch (e: ApiException){
            //Inicio de sesion fallido
            Log.w(TAG, "Inicio de sesion fallido", e)
            Toast.makeText(this, "Inicio de sesion fallido", Toast.LENGTH_SHORT).show()

        }

    }

}