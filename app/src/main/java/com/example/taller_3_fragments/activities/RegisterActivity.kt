package com.example.taller_3_fragments.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.error
import com.example.taller_3_fragments.R
import com.example.taller_3_fragments.utils.UserProfileManager

class RegisterActivity : AppCompatActivity() {

    // Declara las vistas del formulario de registro
    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var telefonoEditText: EditText
    private lateinit var correoEditText: EditText
    private lateinit var confirmarCorreoEditText: EditText
    private lateinit var contrasenaEditText: EditText
    private lateinit var confirmarContrasenaEditText: EditText
    private lateinit var aceptarTerminosCheckBox: CheckBox
    private lateinit var registrarButton: Button
    private lateinit var tv_loginRegister: TextView
    private lateinit var btnVolverInicio: LinearLayout


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro) // Asumo que este es tu layout con los campos

        // Inicializar vistas del formulario
        nombreEditText = findViewById(R.id.editTextNombre)
        apellidoEditText = findViewById(R.id.editTextApellido)
        telefonoEditText = findViewById(R.id.editTextTelefono)
        correoEditText = findViewById(R.id.editTextCorreo)
        confirmarCorreoEditText = findViewById(R.id.editTextConfirmarCorreo)
        contrasenaEditText = findViewById(R.id.editTextContrasena)
        confirmarContrasenaEditText = findViewById(R.id.editTextConfirmarContrasena)
        aceptarTerminosCheckBox = findViewById(R.id.checkBoxAceptar)
        registrarButton =  findViewById(R.id.buttonRegistro)

        // Vistas existentes para navegación
        tv_loginRegister = findViewById(R.id.tv_loginRegister)
        btnVolverInicio = findViewById(R.id.btnVolverInicio)

        tv_loginRegister.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Listener para el botón de registro
        registrarButton.setOnClickListener {
            handleTemporaryRegistration()
        }
    }

    private fun handleTemporaryRegistration() {
        val nombre = nombreEditText.text.toString().trim()
        val apellido = apellidoEditText.text.toString().trim()
        val telefono = telefonoEditText.text.toString().trim()
        val correo = correoEditText.text.toString().trim()
        val confirmarCorreo = confirmarCorreoEditText.text.toString().trim()
        val contrasena = contrasenaEditText.text.toString() // No trimear contraseña
        val confirmarContrasena = confirmarContrasenaEditText.text.toString()

        if (!validateTemporaryInput(
                nombre,
                apellido,
                correo,
                confirmarCorreo,
                contrasena,
                confirmarContrasena
            )
        ) {
            return // Detiene el proceso si la validación falla
        }

        if (!aceptarTerminosCheckBox.isChecked) {
            Toast.makeText(this, "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT)
                .show()
            return
        }



        // La contraseña se guarda en texto plano en SharedPreferences.
        try {
            UserProfileManager.saveUserProfile(
                this,
                "$nombre $apellido",
                correo,
                telefono,
                null,
                contrasena,
                null
            )

            Toast.makeText(this, "¡Registro exitoso!", Toast.LENGTH_LONG).show()

            // Navegar a la pantalla de Login después del registro
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        } catch (e: Exception) {
            Toast.makeText(this, "Error en el registro: ${e.message}", Toast.LENGTH_LONG).show()
             Log.e("RegisterActivity", "Error al guardar perfil", e)
        }
    }

    private fun validateTemporaryInput(
        nombre: String,
        apellido: String,
        correo: String,
        confirmarCorreo: String,
        contrasena: String,
        confirmarContrasena: String
    ): Boolean {
        if (nombre.isEmpty()) {
            nombreEditText.error = "El nombre es obligatorio"
            nombreEditText.requestFocus()
            return false
        }
        if (apellido.isEmpty()) {
            apellidoEditText.error = "El apellido es obligatorio"
            apellidoEditText.requestFocus()
            return false
        }
        if (apellido.isEmpty()) {
            telefonoEditText.error = "El apellido es obligatorio"
            telefonoEditText.requestFocus()
            return false
        }
        if (correo.isEmpty()) {
            correoEditText.error = "El correo es obligatorio"
            correoEditText.requestFocus()
            return false
        }
        if (confirmarCorreo.isEmpty()) {
            confirmarCorreoEditText.error = "La confirmación de correo es obligatoria"
            confirmarCorreoEditText.requestFocus()
            return false
        }
        if (contrasena.isEmpty()) {
            contrasenaEditText.error = "La contraseña es obligatoria"
            contrasenaEditText.requestFocus()
            return false
        }
        if (confirmarContrasena.isEmpty()) {
            confirmarContrasenaEditText.error = "La confirmación de contraseña es obligatoria"
            confirmarContrasenaEditText.requestFocus()
            return false
        }
        if (contrasena != confirmarContrasena) {
            confirmarContrasenaEditText.error = "Las contraseñas no coinciden"
            confirmarContrasenaEditText.requestFocus()
            contrasenaEditText.error = "Las contraseñas no coinciden"
            return false
        }
        return true
    }
}