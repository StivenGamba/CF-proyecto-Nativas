package com.example.taller_3_fragments.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button // Asegúrate de tener esta importación
import android.widget.EditText // Importa EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.text
import com.example.taller_3_fragments.R
import com.example.taller_3_fragments.utils.UserProfileManager // Importa tu UserProfileManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class LoginActivity : AppCompatActivity() {

    // Para Google Sign In
    private lateinit var btnGoogle: Button
    private lateinit var mGoogleSignClient: GoogleSignInClient
    private val RC_SIGN_IN = 2554
    private val TAG = "GoogleSignIn"

    // Para Login Normal
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var btnLoginNormal: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // --- Configuración Google Sign In  ---
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
        mGoogleSignClient = GoogleSignIn.getClient(this, gso)
        btnGoogle = findViewById(R.id.buttonGoogleLogin)
        btnGoogle.setOnClickListener {
            signInWithGoogle() //
        }

        // --- Configuración Login Normal ---
        emailEditText = findViewById(R.id.editTextEmail) //
        passwordEditText = findViewById(R.id.editTextPassword)
        btnLoginNormal = findViewById(R.id.buttonLogin)

        btnLoginNormal.setOnClickListener {
            handleNormalLogin()
        }


        // --- Navegación (existente) ---
        val tv_registro = findViewById<TextView>(R.id.tv_Register)
        tv_registro.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }

        val tv_olvidaste = findViewById<TextView>(R.id.tv_ResetPasswordLogin)
        tv_olvidaste.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)

        }

        val btnVolverInicio: LinearLayout = findViewById(R.id.btnVolverInicio)
        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun handleNormalLogin() {
        val enteredEmail = emailEditText.text.toString().trim()
        val enteredPassword = passwordEditText.text.toString()

        if (enteredEmail.isEmpty()) {
            emailEditText.error = "El correo es obligatorio"
            emailEditText.requestFocus()
            return
        }
        if (enteredPassword.isEmpty()) {
            passwordEditText.error = "La contraseña es obligatoria"
            passwordEditText.requestFocus()
            return
        }

        // Recuperar datos guardados usando UserProfileManager
        val storedEmail = UserProfileManager.getUserEmail(this)
        val storedPassword = UserProfileManager.getUserPassword(this)
        val storedUserName = UserProfileManager.getUserName(this)     // Para pasarlo a MainActivity

        //  comparación directa de contraseñas
        if (enteredEmail == storedEmail && enteredPassword == storedPassword) {
            // Inicio de sesión exitoso
            Toast.makeText(this, "Bienvenido ${storedUserName ?: "Usuario"}", Toast.LENGTH_SHORT)
                .show()

            // Ir a la pantalla principal, pasando los datos del usuario
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", storedEmail)
            intent.putExtra("name", storedUserName)
            intent.putExtra("phone", UserProfileManager.getUserPhone(this))
            startActivity(intent)
            finish()
        } else {
            // Credenciales incorrectas
            Toast.makeText(this, "Correo o contraseña incorrectos.", Toast.LENGTH_LONG).show()

        }
    }

    private fun signInWithGoogle() { // Renombrada de singIn a signInWithGoogle
        val signIntent: Intent = mGoogleSignClient.signInIntent
        startActivityForResult(signIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task) // Renombrado para claridad
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) { // Renombrado
        try {
            val account = completedTask.getResult(ApiException::class.java)

            Log.d(TAG, "Inicio de sesión con Google exitoso: ${account?.email}")
            Toast.makeText(this, "Bienvenido ${account?.displayName}", Toast.LENGTH_SHORT).show()

            // Guardar datos de Google en UserProfileManager si es la primera vez
            UserProfileManager.saveUserProfile(
                this,
                account?.displayName,
                account?.email,
                null,
                null,
                null,
                account?.photoUrl?.toString()
            )

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", account?.email)
            intent.putExtra("name", account?.displayName)
            intent.putExtra("photoUrl", account?.photoUrl?.toString())
            startActivity(intent)
            finish()
        } catch (e: ApiException) {
            Log.w(TAG, "Inicio de sesión con Google fallido. Código: ${e.statusCode}", e)
            Toast.makeText(this, "Inicio de sesión con Google fallido.", Toast.LENGTH_SHORT).show()
        }
    }
}