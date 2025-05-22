package com.example.taller_3_fragments.activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.text
import com.example.taller_3_fragments.R
import com.google.android.material.button.MaterialButton
import java.util.Random

class ForgotPasswordActivity : AppCompatActivity() {


    private lateinit var emailEditTextForgotPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contrasena)


        emailEditTextForgotPassword = findViewById(R.id.editTextCorreoRecuperarContrasena)

        val tvRegisterForgotPassword = findViewById<TextView>(R.id.tv_registerFotgotPassword)
        tvRegisterForgotPassword.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        val tvLoginForgotPassword = findViewById<TextView>(R.id.tv_loginFotgotPassword)
        tvLoginForgotPassword.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val btnEnviarCodigo = findViewById<MaterialButton>(R.id.btn_EnviarCodigo)
        btnEnviarCodigo.setOnClickListener {
            val email = emailEditTextForgotPassword.text.toString().trim()

            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditTextForgotPassword.error = "Ingresa un correo válido"
                emailEditTextForgotPassword.requestFocus()
                return@setOnClickListener
            }

            // Simular envío de código y preparar para navegar
            simulateSendCodeAndPrepareNavigate(email)
        }

        //tv ir a inicio
        val btnVolverInicio: LinearLayout = findViewById(R.id.btnVolverInicio)
        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun generateRandomCode(length: Int = 6): String {
        val random = Random()
        val code = StringBuilder()
        repeat(length) {
            code.append(random.nextInt(10)) // Genera dígitos del 0 al 9
        }
        return code.toString()
    }

    private fun simulateSendCodeAndPrepareNavigate(email: String) {
        val generatedCode = generateRandomCode()
        val toastMessage = "Tu código fue enviado: $generatedCode"

        // Crear el Toast
        val toast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG)


        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100)

        // Mostrar el Toast
        toast.show()

        val intent = Intent(this, EnviarCodigoActivity::class.java)
        intent.putExtra("USER_EMAIL", email)
        intent.putExtra("SIMULATED_CODE", generatedCode)

        startActivity(intent)
        finish()
    }
}