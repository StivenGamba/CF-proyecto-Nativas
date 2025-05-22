package com.example.taller_3_fragments.activities

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.taller_3_fragments.R

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contrasena)

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

        val btnEnviarCodigo = findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_EnviarCodigo)
        btnEnviarCodigo.setOnClickListener {
            val intent = Intent(this, EnviarCodigoActivity::class.java)
            startActivity(intent)
            finish()
        }

        //tv ir a inicio
        val btnVolverInicio: LinearLayout = findViewById(R.id.btnVolverInicio)
        btnVolverInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }



    }

}
