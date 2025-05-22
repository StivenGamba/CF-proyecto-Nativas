package com.example.taller_3_fragments.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.taller_3_fragments.R
import android.widget.TextView


class RegisterActivity : AppCompatActivity(){


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val tv_loginRegister = findViewById<TextView>(R.id.tv_loginRegister)
        tv_loginRegister.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
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