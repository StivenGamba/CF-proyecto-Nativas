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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.taller_3_fragments.R
import com.example.taller_3_fragments.utils.UserProfileManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class perfilFragment : Fragment() {

    private lateinit var tvNombrePerfil: TextView
    private lateinit var tvCorreoPerfil: TextView
    private lateinit var tvCelularPerfil: TextView
    private lateinit var tvDireccionPerfil: TextView
    private lateinit var imgAvatarPerfil: ImageView
    private lateinit var btnLogOut: Button
    private lateinit var btnInfoPersonal: LinearLayout

    private lateinit var googleSignInClient: GoogleSignInClient

    // ActivityResultLauncher para seleccionar imagen de la galería
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                if (selectedImageUri != null) {
                    // Guardar solo la URI de la imagen
                    val context = requireContext()
                    UserProfileManager.saveUserProfile(
                        context,
                        UserProfileManager.getUserName(context),
                        UserProfileManager.getUserEmail(context),
                        UserProfileManager.getUserPhone(context),
                        UserProfileManager.getUserAddress(context),
                        selectedImageUri.toString() // Guardar como String
                    )
                    cargarImagenPerfil(selectedImageUri)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        // Inicializar las vistas del perfil
        tvNombrePerfil = view.findViewById(R.id.tv_nombre_perfil)
        tvCorreoPerfil = view.findViewById(R.id.tv_correo_perfil)
        tvCelularPerfil = view.findViewById(R.id.tv_celular_perfil)
        tvDireccionPerfil = view.findViewById(R.id.tv_direccion_perfil)
        imgAvatarPerfil = view.findViewById(R.id.img_user)
        btnLogOut = view.findViewById(R.id.btnlogOut)


        // Inicializar el LinearLayout clickeable
        btnInfoPersonal = view.findViewById(R.id.btn_info_personal)


        // Configurar Google Sign-In Client (necesario para signOut)
        // Asegúrate de que las opciones sean las mismas que usaste para iniciar sesión
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            // .requestIdToken(getString(R.string.default_web_client_id)) // ID Token para backend
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cargarDatosUsuarioDesdeSharedPreferences()

        // listener para el LinearLayout de "Información Personal"
        btnInfoPersonal.setOnClickListener {
            findNavController().navigate(R.id.action_perfilFragment_to_editarInfoPersonalFragment)
        }

        // Permitir cambiar la imagen al hacer clic en ella
        imgAvatarPerfil.setOnClickListener {
            abrirGaleriaParaSeleccionarImagen()
        }

        // Listener para el botón de LogOut
        btnLogOut.setOnClickListener {
            cerrarSesion()
        }

        // lógica para cargar datos de Google si es la primera vez)
        if (!UserProfileManager.hasUserProfile(requireContext())) {
            val nombreDeGoogle = "Nombre de Google"
            val correoDeGoogle = "correo@google.com"
            UserProfileManager.saveUserProfile(
                requireContext(),
                name = nombreDeGoogle,
                email = correoDeGoogle,
                phone = null,
                address = null,
                imageUri = null
            )
            cargarDatosUsuarioDesdeSharedPreferences()
        }
    }


    private fun cargarDatosUsuarioDesdeSharedPreferences() {
        val context = requireContext()
        tvNombrePerfil.text = UserProfileManager.getUserName(context) ?: getString(R.string.no_disponible)
        tvCorreoPerfil.text = UserProfileManager.getUserEmail(context) ?: getString(R.string.no_disponible)
        val celular = UserProfileManager.getUserPhone(context)
        tvCelularPerfil.text = if (celular.isNullOrEmpty()) getString(R.string.a_adir_n_mero_de_celular) else celular
        val direccion = UserProfileManager.getUserAddress(context)
        tvDireccionPerfil.text = if (direccion.isNullOrEmpty()) getString(R.string.agrega_una_direcci_n_de_entrega) else direccion

        val imageUriString = UserProfileManager.getUserImageUri(context)
        if (imageUriString != null) {
            cargarImagenPerfil(Uri.parse(imageUriString))
        } else {
            Glide.with(this)
                .load(R.drawable.imguser)
                .circleCrop()
                .into(imgAvatarPerfil)
        }
    }

    private fun cargarImagenPerfil(imageUri: Uri) {
        Glide.with(this)
            .load(imageUri)
            .placeholder(R.drawable.imguser) // Placeholder mientras carga
            .error(R.drawable.imguser)       // Imagen de error si falla la carga
            .circleCrop()                    // Para hacer la imagen circular
            .into(imgAvatarPerfil)
    }

    private fun abrirGaleriaParaSeleccionarImagen() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    fun actualizarDatosDeGoogleEnSharedPreferences(nombre: String?, email: String?, fotoUrlGoogle: String?) {
        val context = requireContext()
        // Guardamos nombre y email. Celular, dirección e imagen local se mantienen o se actualizan después.
        UserProfileManager.saveUserProfile(
            context,
            name = nombre,
            email = email,
            phone = UserProfileManager.getUserPhone(context), // Mantiene el existente si hay
            address = UserProfileManager.getUserAddress(context), // Mantiene el existente si hay
            imageUri = UserProfileManager.getUserImageUri(context) // Mantiene la URI local si hay
            // o podrías guardar fotoUrlGoogle si decides usarla
            // como fallback o si el usuario no elige una local.
        )
        // Actualiza la UI inmediatamente
        cargarDatosUsuarioDesdeSharedPreferences()

        // Si tienes fotoUrlGoogle y no hay una imagen local seleccionada, podrías cargarla:
        if (fotoUrlGoogle != null && UserProfileManager.getUserImageUri(context) == null) {
            Glide.with(this)
                .load(fotoUrlGoogle)
                .placeholder(R.drawable.imguser)
                .error(R.drawable.imguser)
                .circleCrop()
                .into(imgAvatarPerfil)
            // Opcionalmente, podrías guardar esta fotoUrlGoogle en SharedPreferences
            // si quieres que persista como la imagen por defecto hasta que el usuario elija otra.
        }
    }

    private fun cerrarSesion() {
        // 1. Cerrar sesión de Google (si se inició sesión con Google)
        googleSignInClient.signOut().addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Sesión de Google cerrada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error al cerrar sesión de Google", Toast.LENGTH_SHORT).show()
            }

            // 2. Borrar datos locales del usuario de SharedPreferences
            UserProfileManager.clearUserProfile(requireContext())
            Toast.makeText(context, "Datos locales borrados", Toast.LENGTH_SHORT).show()

            try {
                findNavController().navigate(R.id.action_perfilFragment_to_loginFragment, null)
                // Si quieres limpiar el backstack hasta un destino específico (ej. tu pantalla de inicio):
                // findNavController().navigate(R.id.action_perfilFragment_to_loginFragment, null, NavOptions.Builder()
                //    .setPopUpTo(R.id.tu_pantalla_de_inicio_del_grafo, true) // 'true' para incluir el destino del popUpTo
                //    .build())

                // O, si loginFragment es el inicio de tu grafo y quieres limpiar todo el stack:
                // findNavController().navigate(R.id.action_perfilFragment_to_loginFragment, null, NavOptions.Builder()
                //    .setPopUpTo(findNavController().graph.startDestinationId, true)
                //    .build())

                // Otra forma más simple si tu login es el startDestination del nav_graph
                // y quieres limpiar todo el backstack.
                // activity?.finish() // Cierra la actividad actual
                // startActivity(Intent(requireActivity(), LoginActivity::class.java)) // Inicia la LoginActivity
                // La mejor opción depende de cómo esté estructurada tu navegación (Single Activity vs Multiple Activities)
            } catch (e: IllegalArgumentException) {
                // Esto puede ocurrir si la acción de navegación no se encuentra.
                // Maneja el error, por ejemplo, mostrando un mensaje o navegando a un destino por defecto.
                Toast.makeText(context, "Error de navegación: ${e.message}", Toast.LENGTH_LONG).show()
                // Como fallback, podrías intentar reiniciar la actividad principal si tu app tiene una
                // lógica que redirige al login si no hay sesión.
                // val intent = Intent(requireActivity(), MainActivity::class.java) // O tu actividad principal
                // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                // startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar los datos por si cambiaron en otro fragmento y volvimos a este
        cargarDatosUsuarioDesdeSharedPreferences()
    }
}