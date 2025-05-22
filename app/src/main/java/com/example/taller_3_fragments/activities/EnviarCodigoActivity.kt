package com.example.taller_3_fragments.activities

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
import androidx.compose.ui.semantics.text
import com.example.taller_3_fragments.R
import com.google.android.material.button.MaterialButton // Asegúrate de tener esta importación

class EnviarCodigoActivity : AppCompatActivity() {

    private lateinit var textViewCronometro: TextView
    private var countDownTimer: CountDownTimer? = null

    private lateinit var etBox1: EditText
    private lateinit var etBox2: EditText
    private lateinit var etBox3: EditText
    private lateinit var etBox4: EditText
    private lateinit var etBox5: EditText
    private lateinit var etBox6: EditText

    private lateinit var tvSolicitarNuevoCodigo: TextView

    // Variables para almacenar los datos recibidos
    private var userEmail: String? = null
    private var simulatedCodeFromPreviousActivity: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validarcodigo)

        textViewCronometro = findViewById(R.id.textViewCronometro)
        tvSolicitarNuevoCodigo = findViewById(R.id.tv_solicitarNuevoCodigo)

        etBox1 = findViewById(R.id.et_box1)
        etBox2 = findViewById(R.id.et_box2)
        etBox3 = findViewById(R.id.et_box3)
        etBox4 = findViewById(R.id.et_box4)
        etBox5 = findViewById(R.id.et_box5)
        etBox6 = findViewById(R.id.et_box6)

        // Obtener datos del Intent
        userEmail = intent.getStringExtra("USER_EMAIL")
        simulatedCodeFromPreviousActivity = intent.getStringExtra("SIMULATED_CODE")

        // Es buena práctica verificar si los datos esenciales llegaron
        if (userEmail == null || simulatedCodeFromPreviousActivity == null) {
            Toast.makeText(this, "Error: Faltan datos para continuar el proceso.", Toast.LENGTH_LONG).show()
            // Podrías redirigir al Login o a la pantalla anterior si esto ocurre
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return // Termina onCreate si faltan datos
        }


        //listener btn verificar
        val buttonVerificar: MaterialButton = findViewById(R.id.buttonVerificar)
        buttonVerificar.setOnClickListener {
            handleVerifyCode()
        }

        // ... (resto de tus listeners para tvRegisterValidarCodigo, tvLoginValidarCodigo, btnVolverInicio sin cambios) ...
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
        setupSolicitarNuevoCodigo()
        startTimer()
    }

    private fun getEnteredOtp(): String {
        return "${etBox1.text}${etBox2.text}${etBox3.text}${etBox4.text}${etBox5.text}${etBox6.text}"
    }

    private fun handleVerifyCode() {
        val enteredOtp = getEnteredOtp()

        if (enteredOtp.length < 6) {
            Toast.makeText(this, "Por favor, ingresa el código completo.", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar el código
        if (enteredOtp == simulatedCodeFromPreviousActivity) {
            Toast.makeText(this, "Código verificado correctamente.", Toast.LENGTH_SHORT).show()
            countDownTimer?.cancel() // Detener el cronómetro si el código es correcto

            // Navegar a la actividad para crear la nueva contraseña
            val intent = Intent(this, CrearNuevaContrasenaActivity::class.java) // CAMBIA CrearNuevaContrasenaActivity por el nombre real de tu actividad
            intent.putExtra("USER_EMAIL", userEmail) // Pasar el email a la siguiente actividad
            // No es necesario pasar el código aquí, ya fue verificado
            startActivity(intent)
            finish() // Cierra esta actividad
        } else {
            Toast.makeText(this, "Código incorrecto. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
            resetOtpState() // Limpiar los campos para un nuevo intento
        }
    }


    private fun setupOtpInput() {
        // Tu código existente aquí...
        etBox1.addTextChangedListener(GenericTextWatcher(etBox1, etBox2))
        etBox2.addTextChangedListener(GenericTextWatcher(etBox2, etBox3))
        etBox3.addTextChangedListener(GenericTextWatcher(etBox3, etBox4))
        etBox4.addTextChangedListener(GenericTextWatcher(etBox4, etBox5))
        etBox5.addTextChangedListener(GenericTextWatcher(etBox5, etBox6))
        etBox6.addTextChangedListener(GenericTextWatcher(etBox6, null)) // Aquí puedes llamar a handleVerifyCode() si quieres verificar automáticamente cuando se llena el último campo

        // Opcional: Verificar automáticamente cuando se llena el último campo
        etBox6.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1 && getEnteredOtp().length == 6) {
                    // Opcionalmente, podrías llamar a buttonVerificar.performClick()
                    // o directamente a handleVerifyCode() aquí
                    // buttonVerificar.performClick()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        etBox2.setOnKeyListener(GenericKeyEvent(etBox2, etBox1))
        etBox3.setOnKeyListener(GenericKeyEvent(etBox3, etBox2))
        etBox4.setOnKeyListener(GenericKeyEvent(etBox4, etBox3))
        etBox5.setOnKeyListener(GenericKeyEvent(etBox5, etBox4))
        etBox6.setOnKeyListener(GenericKeyEvent(etBox6, etBox5))
    }

    private fun startTimer() {
        tvSolicitarNuevoCodigo.isEnabled = false
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                textViewCronometro.text = String.format("%02d:%02d", minutes, seconds)
                tvSolicitarNuevoCodigo.isEnabled = false
            }

            override fun onFinish() {
                textViewCronometro.text = "00:00"
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
                Toast.makeText(this, "Solicitando nuevo código... (Simulación)", Toast.LENGTH_SHORT).show()
                // En una app real, aquí llamarías a tu backend para reenviar un código.
                // Para la simulación, podemos generar un nuevo código y mostrarlo en un Toast,
                // y actualizar `simulatedCodeFromPreviousActivity`.
                val newSimulatedCode = (100000..999999).random().toString() // Genera un nuevo código de 6 dígitos
                simulatedCodeFromPreviousActivity = newSimulatedCode // Actualiza el código esperado

                val toast = Toast.makeText(this, "Nuevo código simulado: $newSimulatedCode", Toast.LENGTH_LONG)
                // Posicionar el Toast arriba si es necesario
                // toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100)
                toast.show()

                resetOtpState()
                startTimer()
            }
        }
    }

    private fun resetOtpState() {
        etBox1.text.clear()
        etBox2.text.clear()
        etBox3.text.clear()
        etBox4.text.clear()
        etBox5.text.clear()
        etBox6.text.clear()
        etBox1.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    // ... (Clases internas GenericTextWatcher y GenericKeyEvent sin cambios) ...
    inner class GenericTextWatcher(
        private val currentView: EditText,
        private val nextView: EditText?
    ) : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            val text = editable.toString()
            if (text.length == 1) {
                nextView?.requestFocus()
                // Podrías añadir un check aquí también si nextView es null y el código está completo
                // if (nextView == null && getEnteredOtp().length == 6) {
                //     findViewById<MaterialButton>(R.id.buttonVerificar).performClick()
                // }
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
                // previousView.selectAll() // SelectAll puede ser un poco agresivo si el usuario solo quiere borrar un carácter anterior.
                // A menudo es mejor solo mover el foco.
                return true
            }
            return false
        }
    }
}