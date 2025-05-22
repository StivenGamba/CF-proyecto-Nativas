package com.example.taller_3_fragments.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taller_3_fragments.R
import com.google.android.material.button.MaterialButton

class CrearNuevaContrasenaActivity : AppCompatActivity() {

    private lateinit var etNuevaContrasena: EditText
    private lateinit var etConfirmarNuevaContrasena: EditText
    private lateinit var btnCambiarContrasena: MaterialButton
    private lateinit var tvLoginConContrasena: TextView
    private lateinit var tvRegisterConContrasena: TextView

    private var userEmail: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_nueva_contrasena)

        // Inicializar vistas desde el layout
        etNuevaContrasena = findViewById(R.id.et_nuevaContrasena)
        etConfirmarNuevaContrasena = findViewById(R.id.et_confirmarNuevaContrasena)
        btnCambiarContrasena = findViewById(R.id.btn_cambiarContrasena)
        tvLoginConContrasena = findViewById(R.id.tv_loginConContrasena)
        tvRegisterConContrasena = findViewById(R.id.tv_registerConContrasena)

        // Obtener el email del intent
        userEmail = intent.getStringExtra("USER_EMAIL")

         val tvMensajeUsuario: TextView? = findViewById(R.id.tv_mensajeUsuarioCorreo)
         tvMensajeUsuario?.text = "Establece una nueva contraseña para $userEmail"

        if (userEmail == null) {

            Toast.makeText(this, "Error: No se pudo obtener la información del usuario.", Toast.LENGTH_LONG).show()
            navigateToLogin()
            return
        }


        btnCambiarContrasena.setOnClickListener {
            handleChangePassword()
        }

        tvLoginConContrasena.setOnClickListener {
            navigateToLogin()
        }

        tvRegisterConContrasena.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)

            startActivity(intent)
            // finish()
        }
    }

    private fun handleChangePassword() {
        val nuevaContrasena = etNuevaContrasena.text.toString().trim()
        val confirmarContrasena = etConfirmarNuevaContrasena.text.toString().trim()

        //  Validar campos vacíos
        if (nuevaContrasena.isEmpty()) {
            etNuevaContrasena.error = "La nueva contraseña no puede estar vacía"
            etNuevaContrasena.requestFocus()
            return
        }
        if (confirmarContrasena.isEmpty()) {
            etConfirmarNuevaContrasena.error = "Confirma tu nueva contraseña"
            etConfirmarNuevaContrasena.requestFocus()
            return
        }

        //  Validar longitud mínima
        val longitudMinima = 6
        if (nuevaContrasena.length < longitudMinima) {
            etNuevaContrasena.error = "La contraseña debe tener al menos $longitudMinima caracteres"
            etNuevaContrasena.requestFocus()
            return
        }

        //  Validar que las contraseñas coincidan
        if (nuevaContrasena != confirmarContrasena) {
            etConfirmarNuevaContrasena.error = "Las contraseñas no coinciden"
            etConfirmarNuevaContrasena.requestFocus()
            // También podría ser útil limpiar el campo de confirmación
            etConfirmarNuevaContrasena.text.clear()
            return
        }


        Toast.makeText(this, "Contraseña actualizada exitosamente para $userEmail (Simulación)", Toast.LENGTH_LONG).show()

        // Navegar a la pantalla de Login y limpiar el stack
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        // Estas flags aseguran que al ir al Login, se limpien todas las actividades anteriores
        // del proceso de recuperación de contraseña del back stack.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Cierra esta actividad
    }
}