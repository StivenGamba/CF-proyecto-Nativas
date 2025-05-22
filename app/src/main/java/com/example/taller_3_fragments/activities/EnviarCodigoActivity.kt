package com.example.taller_3_fragments.activities // Asegúrate que el package sea el correcto

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taller_3_fragments.R

class EnviarCodigoActivity : AppCompatActivity() {

    private lateinit var textViewCronometro: TextView
    private var countDownTimer: CountDownTimer? = null

    private lateinit var etBox1: EditText
    private lateinit var etBox2: EditText
    private lateinit var etBox3: EditText
    private lateinit var etBox4: EditText
    private lateinit var etBox5: EditText
    private lateinit var etBox6: EditText

    private lateinit var tvSolicitarNuevoCodigo: TextView // << NUEVO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validarcodigo)

        textViewCronometro = findViewById(R.id.textViewCronometro)
        tvSolicitarNuevoCodigo = findViewById(R.id.tv_solicitarNuevoCodigo) // << INICIALIZAR

        // Inicializar los EditText
        etBox1 = findViewById(R.id.et_box1)
        etBox2 = findViewById(R.id.et_box2)
        etBox3 = findViewById(R.id.et_box3)
        etBox4 = findViewById(R.id.et_box4)
        etBox5 = findViewById(R.id.et_box5)
        etBox6 = findViewById(R.id.et_box6)

        //listener btn verificar
        val buttonVerificar: com.google.android.material.button.MaterialButton = findViewById(R.id.buttonVerificar)
        buttonVerificar.setOnClickListener {
        //logica para verificar codigo
        }

        //tv ir a registro
        val tvRegisterValidarCodigo: TextView = findViewById(R.id.tv_RegisterValidarCodigo)
        tvRegisterValidarCodigo.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        //tv ir a login
        val tvLoginValidarCodigo: TextView = findViewById(R.id.tv_loginValidarCodigo)
        tvLoginValidarCodigo.setOnClickListener {
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




        setupOtpInput()
        setupSolicitarNuevoCodigo() // << NUEVO: Configurar el listener

        startTimer()
    }

    private fun setupOtpInput() {
        // ... (código de GenericTextWatcher y GenericKeyEvent sin cambios)
        etBox1.addTextChangedListener(GenericTextWatcher(etBox1, etBox2))
        etBox2.addTextChangedListener(GenericTextWatcher(etBox2, etBox3))
        etBox3.addTextChangedListener(GenericTextWatcher(etBox3, etBox4))
        etBox4.addTextChangedListener(GenericTextWatcher(etBox4, etBox5))
        etBox5.addTextChangedListener(GenericTextWatcher(etBox5, etBox6))
        etBox6.addTextChangedListener(GenericTextWatcher(etBox6, null))

        etBox2.setOnKeyListener(GenericKeyEvent(etBox2, etBox1))
        etBox3.setOnKeyListener(GenericKeyEvent(etBox3, etBox2))
        etBox4.setOnKeyListener(GenericKeyEvent(etBox4, etBox3))
        etBox5.setOnKeyListener(GenericKeyEvent(etBox5, etBox4))
        etBox6.setOnKeyListener(GenericKeyEvent(etBox6, etBox5))
    }

    private fun startTimer() {
        // Deshabilitar el botón de solicitar nuevo código al iniciar el timer
        tvSolicitarNuevoCodigo.isEnabled = false
        // El ColorStateList se encargará de cambiar el color automáticamente

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(60000, 1000) { // 1 minutos
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                textViewCronometro.text = String.format("%02d:%02d", minutes, seconds)
                tvSolicitarNuevoCodigo.isEnabled = false // Asegurarse que sigue deshabilitado durante el conteo
            }

            override fun onFinish() {
                textViewCronometro.text = "00:00"
                // Habilitar el TextView para solicitar nuevo código
                tvSolicitarNuevoCodigo.isEnabled = true
                tvSolicitarNuevoCodigo.isClickable = true
                tvSolicitarNuevoCodigo.isFocusable = true

                Toast.makeText(this@EnviarCodigoActivity, "Puedes solicitar un nuevo código.", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }

    private fun setupSolicitarNuevoCodigo() {
        tvSolicitarNuevoCodigo.setOnClickListener {
            if (tvSolicitarNuevoCodigo.isEnabled) {
                // Lógica para reenviar el código
                Toast.makeText(this, "Solicitando nuevo código...", Toast.LENGTH_SHORT).show()
                 resetOtpState() //  Limpiar los campos OTP
                startTimer() // Reinicia el cronómetro y deshabilita el tvSolicitarNuevoCodigo de nuevo
            }
        }
    }

    // Función para limpiar los campos del OTP
     private fun resetOtpState() {
         etBox1.text.clear()
         etBox2.text.clear()
         etBox3.text.clear()
         etBox4.text.clear()
         etBox5.text.clear()
         etBox6.text.clear()
         etBox1.requestFocus() // Poner foco en el primer campo
     }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    // ... (Clases internas GenericTextWatcher y GenericKeyEvent sin cambios)
    inner class GenericTextWatcher(
        private val currentView: EditText,
        private val nextView: EditText?
    ) : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            val text = editable.toString()
            if (text.length == 1) {
                nextView?.requestFocus()
                nextView?.setSelection(nextView.text.length)
            }
        }
        override fun beforeTextChanged(arg0: CharSequence?, arg1: Int, arg2: Int, arg3: Int) {}
        override fun onTextChanged(arg0: CharSequence?, arg1: Int, arg2: Int, arg3: Int) {}
    }

    inner class GenericKeyEvent(
        private val currentView: EditText,
        private val previousView: EditText?
    ) : View.OnKeyListener {
        override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event?.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_DEL &&
                currentView.id != R.id.et_box1 &&
                currentView.text.isEmpty() &&
                previousView != null) {
                previousView.requestFocus()
                previousView.selectAll()
                return true
            }
            return false
        }
    }
}