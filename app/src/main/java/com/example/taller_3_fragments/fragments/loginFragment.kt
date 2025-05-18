package com.example.taller_3_fragments.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.taller_3_fragments.R
import com.example.taller_3_fragments.activities.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class loginFragment : Fragment() {

    private lateinit var btnGoogle: Button
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    // Constantes para request codes y tags
    companion object {

        private const val RC_SIGN_IN = 123 //
        private const val TAG = "GoogleSignIn"
    }

    // ActivityResultLauncher para manejar el resultado de Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        } else {
            // Sign in cancelado o fallido
            Log.w(TAG, "Google sign in fallido o cancelado. Resultado: ${result.resultCode}")
            Toast.makeText(requireContext(), "Inicio de sesión con Google cancelado o fallido", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar Google Sign In Options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile() // Has solicitado el perfil, así que puedes acceder a displayName
            // .requestIdToken(getString(R.string.default_web_client_id)) // Descomenta si necesitas el ID Token para backend
            .build()

        // Crear el cliente de Google SignIn
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnGoogle = view.findViewById(R.id.buttonGoogleLogin) //

        btnGoogle.setOnClickListener {
            signIn()
        }

        // Opcional: Verificar si el usuario ya ha iniciado sesión al crear la vista
        // val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        // if (account != null) {
        //     navigateToMainActivity(account)
        // }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent) // Usar el nuevo launcher
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            Log.d(TAG, "signInSuccess: ${account?.email}")
            Toast.makeText(requireContext(), "Bienvenido ${account?.displayName}", Toast.LENGTH_SHORT).show()
            navigateToPerfilFragment(account)

        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            Toast.makeText(requireContext(), "Error al iniciar sesión con Google: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun navigateToPerfilFragment(account: GoogleSignInAccount?) {
        val bundle = Bundle().apply {
            putString("userEmail", account?.email)
            putString("userName", account?.displayName)
        }
        findNavController().navigate(R.id.action_loginFragment_to_perfilFragment, bundle)
    }


}