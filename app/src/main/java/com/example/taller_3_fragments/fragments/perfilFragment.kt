package com.example.taller_3_fragments.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.taller_3_fragments.R
import com.example.taller_3_fragments.utils.UserProfileManager
import com.example.taller_3_fragments.viewmodels.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class perfilFragment : Fragment() {

    // Vistas
    private lateinit var tvNombrePerfil: TextView
    private lateinit var tvCorreoPerfil: TextView
    private lateinit var tvCelularPerfil: TextView
    private lateinit var tvDireccionPerfil: TextView
    private lateinit var imgAvatarPerfil: ImageView
    private lateinit var btnLogOut: Button
    private lateinit var btnLogin: Button
    private lateinit var btnInfoPersonal: LinearLayout

    // ViewModel
    private lateinit var userViewModel: UserViewModel

    // Google Sign-In
    private lateinit var googleSignInClient: GoogleSignInClient

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                if (selectedImageUri != null) {
                    val context = requireContext()
                    UserProfileManager.saveUserImageUri(context, selectedImageUri.toString())
                    cargarImagenPerfil(selectedImageUri)
                }
            }
        }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        tvNombrePerfil = view.findViewById(R.id.tv_nombre_perfil)
        tvCorreoPerfil = view.findViewById(R.id.tv_correo_perfil)
        tvCelularPerfil = view.findViewById(R.id.tv_celular_perfil)
        tvDireccionPerfil = view.findViewById(R.id.tv_direccion_perfil)
        imgAvatarPerfil = view.findViewById(R.id.img_user)
        btnLogOut = view.findViewById(R.id.btnlogOut)
        btnLogin = view.findViewById(R.id.btnlogin)
        btnInfoPersonal = view.findViewById(R.id.btn_info_personal)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        observeUserProfile() // La lógica de visibilidad de botones
        cargarDatosAdicionalesDesdeSharedPreferences()

        btnInfoPersonal.setOnClickListener {
            // Solo permitir si está logueado
            if (userViewModel.userProfile.value != null) {
                findNavController().navigate(R.id.action_perfilFragment_to_editarInfoPersonalFragment)
            } else {
                Toast.makeText(context, "Debes iniciar sesión para ver tu información", Toast.LENGTH_SHORT).show()

            }
        }

        imgAvatarPerfil.setOnClickListener {
            if (userViewModel.userProfile.value != null) { // Solo permitir cambiar si está logueado
                abrirGaleriaParaSeleccionarImagen()
            }
        }

        btnLogOut.setOnClickListener {
            cerrarSesion()
        }

        btnLogin.setOnClickListener {
            // Navegar al fragmento de login
            // Asegúrate de que esta acción exista en tu grafo de navegación desde perfilFragment
            try {
                findNavController().navigate(R.id.action_perfilFragment_to_loginFragment)
            } catch (e: IllegalArgumentException) {
                Toast.makeText(context, "Error de navegación a login: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun observeUserProfile() {
        userViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            val isLoggedIn = userProfile != null // Determina si el usuario está logueado

            if (isLoggedIn) {
                // Usuario Logueado
                tvNombrePerfil.text = userProfile?.displayName ?: getString(R.string.no_disponible)
                tvCorreoPerfil.text = userProfile?.email ?: getString(R.string.no_disponible)

                val localImageUriString = UserProfileManager.getUserImageUri(requireContext())
                if (localImageUriString != null) {
                    cargarImagenPerfil(Uri.parse(localImageUriString))
                } else if (userProfile?.photoUrl != null) {
                    cargarImagenPerfil(userProfile.photoUrl)
                } else {
                    mostrarImagenPorDefecto()
                }

                // --- Control de visibilidad de BOTONES del fragmento ---
                btnLogOut.visibility = View.VISIBLE
                btnLogin.visibility = View.GONE // Ocultar botón de login si ya está logueado
                btnInfoPersonal.visibility = View.VISIBLE // Mostrar info personal
                // También puedes habilitar/deshabilitar la imagen de perfil para cambiarla
                imgAvatarPerfil.isClickable = true


            } else {
                // Usuario NO Logueado o perfil borrado
                tvNombrePerfil.text = getString(R.string.no_disponible )
                tvCorreoPerfil.text = getString(R.string.no_disponible)
                mostrarImagenPorDefecto()

                // --- Control de visibilidad de BOTONES del fragmento ---
                btnLogOut.visibility = View.GONE   // Ocultar botón de logout
                btnLogin.visibility = View.VISIBLE // Mostrar botón de login
                btnInfoPersonal.visibility = View.GONE // Ocultar info personal, o mostrar un mensaje
                // Considera deshabilitar el click en la imagen si no está logueado
                imgAvatarPerfil.isClickable = false


                // Opcional: Si un usuario no logueado llega aquí, podrías forzar la navegación al LoginFragment.
                // Pero esto podría causar un bucle si el LoginFragment es el destino de `action_perfilFragment_to_loginFragment`.
                // Es mejor controlar el acceso a perfilFragment desde el grafo de navegación o la Activity.
            }
        }
    }

    private fun cargarDatosAdicionalesDesdeSharedPreferences() {
        val context = requireContext()
        val celular = UserProfileManager.getUserPhone(context)
        tvCelularPerfil.text = if (celular.isNullOrEmpty()) getString(R.string.a_adir_n_mero_de_celular) else celular
        val direccion = UserProfileManager.getUserAddress(context)
        tvDireccionPerfil.text = if (direccion.isNullOrEmpty()) getString(R.string.agrega_una_direcci_n_de_entrega) else direccion
    }

    private fun cargarImagenPerfil(imageUri: Uri) {
        Glide.with(this)
            .load(imageUri)
            .placeholder(R.drawable.imguser)
            .error(R.drawable.imguser)
            .circleCrop()
            .into(imgAvatarPerfil)
    }

    private fun mostrarImagenPorDefecto() {
        Glide.with(this)
            .load(R.drawable.imguser)
            .circleCrop()
            .into(imgAvatarPerfil)
    }

    private fun abrirGaleriaParaSeleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun cerrarSesion() {
        googleSignInClient.signOut().addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Sesión de Google cerrada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error al cerrar sesión de Google", Toast.LENGTH_SHORT).show()
            }

            userViewModel.clearUserProfile() // Esto disparará el observador en observeUserProfile
            UserProfileManager.clearUserProfile(requireContext())
            Toast.makeText(context, "Datos locales borrados", Toast.LENGTH_SHORT).show()

            // Ya no es necesario navegar explícitamente a loginFragment aquí si el observador maneja la UI
            // Opcionalmente, puedes decidir navegar si el diseño de tu app lo requiere.
            // Por ejemplo, si después de cerrar sesión siempre quieres ir a la pantalla de inicio o login.
            // try {
            //    findNavController().navigate(R.id.action_perfilFragment_to_loginFragment, null)
            // } catch (e: IllegalArgumentException) {
            //    Toast.makeText(context, "Error de navegación: ${e.message}", Toast.LENGTH_LONG).show()
            // }
        }
    }

    override fun onResume() {
        super.onResume()
        // Cargar datos que podrían haber cambiado fuera del fragmento
        cargarDatosAdicionalesDesdeSharedPreferences()

        // Re-evaluar la imagen y el estado del usuario en onResume es buena práctica,
        // especialmente si el userProfile podría cambiar mientras el fragmento está pausado.
        // El observador de LiveData ya debería manejar la mayoría de los cambios de userProfile.
        // Pero para la imagen, si se carga desde SharedPreferences directamente, puede ser útil.
        val currentUserProfile = userViewModel.userProfile.value
        if (currentUserProfile != null) {
            val localImageUriString = UserProfileManager.getUserImageUri(requireContext())
            if (localImageUriString != null) {
                cargarImagenPerfil(Uri.parse(localImageUriString))
            } else if (currentUserProfile.photoUrl != null) {
                cargarImagenPerfil(currentUserProfile.photoUrl)
            } else {
                mostrarImagenPorDefecto()
            }
        } else {
            mostrarImagenPorDefecto()
        }
    }
}