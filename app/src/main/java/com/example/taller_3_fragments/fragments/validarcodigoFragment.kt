package com.example.taller_3_fragments.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.taller_3_fragments.R

class ValidarCodigoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_validarcodigo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textViewCronometro: TextView = view.findViewById(R.id.textViewCronometro)

        object : CountDownTimer(180000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                textViewCronometro.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                textViewCronometro.text = "00:00"
                // Aquí puedes activar un botón o permitir reenviar el código
            }
        }.start()
    }
}
