package com.example.taller_3_fragments.fragments

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
import androidx.compose.ui.semantics.text
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
    private lateinit var btnInfoPersonal: LinearLayout

    // ViewModel
    private lateinit var userViewModel: UserViewModel

    // Google Sign-In
    private lateinit var googleSignInClient: GoogleSignInClient

    // ActivityResultLauncher para seleccionar imagen de la galería
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                if (selectedImageUri != null) {
                    // Guardar la URI de la imagen seleccionada localmente usando UserProfileManager
                    val context = requireContext()
                    UserProfileManager.saveUserImageUri(context, selectedImageUri.toString())
                    cargarImagenPerfil(selectedImageUri) // Cargar inmediatamente la imagen seleccionada
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        // Inicializar las vistas
        tvNombrePerfil = view.findViewById(R.id.tv_nombre_perfil)
        tvCorreoPerfil = view.findViewById(R.id.tv_correo_perfil)
        tvCelularPerfil = view.findViewById(R.id.tv_celular_perfil)
        tvDireccionPerfil = view.findViewById(R.id.tv_direccion_perfil)
        imgAvatarPerfil = view.findViewById(R.id.img_user)
        btnLogOut = view.findViewById(R.id.btnlogOut)
        btnInfoPersonal = view.findViewById(R.id.btn_info_personal)

        // Configurar Google Sign-In Client (necesario para signOut)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar el ViewModel compartido desde la Activity
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        // Observar los datos del UserViewModel (nombre, email, foto de Google)
        observeUserProfile()

        // Cargar datos adicionales (teléfono, dirección, imagen local) desde SharedPreferences
        cargarDatosAdicionalesDesdeSharedPreferences()

        // Listeners
        btnInfoPersonal.setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_editarInfoPersonalFragment)
        }

        imgAvatarPerfil.setOnClickListener {
            abrirGaleriaParaSeleccionarImagen()
        }

        btnLogOut.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun observeUserProfile() {
        userViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            if (userProfile != null) {
                tvNombrePerfil.text = userProfile.displayName ?: getString(R.string.no_disponible)
                tvCorreoPerfil.text = userProfile.email ?: getString(R.string.no_disponible)

                // Cargar imagen: Priorizar imagen local, luego foto de Google, luego placeholder
                val localImageUriString = UserProfileManager.getUserImageUri(requireContext())
                if (localImageUriString != null) {
                    cargarImagenPerfil(Uri.parse(localImageUriString))
                } else if (userProfile.photoUrl != null) {
                    cargarImagenPerfil(userProfile.photoUrl)
                } else {
                    mostrarImagenPorDefecto()
                }
            } else {
                // Estado de "no logueado" o perfil borrado
                tvNombrePerfil.text = getString(R.string.no_disponible)
                tvCorreoPerfil.text = getString(R.string.no_disponible)
                mostrarImagenPorDefecto()
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

            userViewModel.clearUserProfile()
            UserProfileManager.clearUserProfile(requireContext()) // Esto también borrará la imagen local
            Toast.makeText(context, "Datos locales borrados", Toast.LENGTH_SHORT).show()

            try {
                findNavController().navigate(R.id.action_perfilFragment_to_loginFragment, null)
            } catch (e: IllegalArgumentException) {
                Toast.makeText(context, "Error de navegación: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDatosAdicionalesDesdeSharedPreferences()

        // Re-evaluar la imagen al resumir el fragmento
        val localImageUriString = UserProfileManager.getUserImageUri(requireContext())
        val googlePhotoUrl = userViewModel.userProfile.value?.photoUrl // Acceder de forma segura

        if (localImageUriString != null) {
            cargarImagenPerfil(Uri.parse(localImageUriString))
        } else if (googlePhotoUrl != null) {
            cargarImagenPerfil(googlePhotoUrl)
        } else {
            mostrarImagenPorDefecto()
        }
    }
}