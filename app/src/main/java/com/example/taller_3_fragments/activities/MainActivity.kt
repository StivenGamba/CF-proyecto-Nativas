package com.example.taller_3_fragments.activities

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.taller_3_fragments.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.bumptech.glide.Glide
import com.example.taller_3_fragments.viewmodels.UserViewModel

class MainActivity : AppCompatActivity() {
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var navController: NavController? = null

    private lateinit var userViewModel: UserViewModel
    private lateinit var iconPerfilImageView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        iconPerfilImageView = findViewById(R.id.icon_perfil)

        // Inicializar el ViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)


        // Obtener datos del Intent (si vienen de LoginActivity)
        val email = intent.getStringExtra("email")
        val name = intent.getStringExtra("name")
        // Asumimos que también podrías pasar la URL de la foto de perfil desde GoogleSignIn
        val photoUrlString = intent.getStringExtra("photoUrl") // ¡Necesitarás pasar esto desde LoginActivity!

        if (email != null || name != null || photoUrlString != null) {
            userViewModel.setUserProfile(email, name, photoUrlString)
        }
        // Observar cambios en el perfil del usuario para actualizar la UI de MainActivity
        userViewModel.userProfile.observe(this) { userProfile ->
            if (userProfile?.photoUrl != null) {
                Glide.with(this)
                    .load(userProfile.photoUrl)
                    .placeholder(R.drawable.imguser) // Imagen mientras carga
                    .error(R.drawable.imguser)       // Imagen si hay error al cargar
                    .circleCrop() // Opcional: para hacer la imagen circular
                    .into(iconPerfilImageView)
            } else {
                // Si no hay foto de perfil (o el usuario cerró sesión), mostrar la imagen por defecto
                iconPerfilImageView.setImageResource(R.drawable.imguser)
            }
            // Aquí actualizar un TextView con el nombre del usuario en el Toolbar, etc.
        }






        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false) // Quitar botón de navegación del toolbar

        // Initialize navigation controller
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)


        NavigationUI.setupWithNavController(navigationView!!, navController!!)


        bottomNavigationView = findViewById(R.id.bottom_navigation)


        bottomNavigationView?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.inicioFragment -> {
                    navController?.navigate(R.id.inicioFragment)
                    true
                }
                R.id.carritoFragment -> {
                    navController?.navigate(R.id.carritoFragment)
                    true
                }
                R.id.perfilFragment -> {
                    navController?.navigate(R.id.perfilFragment)
                    true
                }
                R.id.navigation_menu -> {
                    // Abrir el drawer cuando se selecciona el ítem de menú
                    drawerLayout?.openDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }


        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                performSearch(searchEditText.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }


        findViewById<View>(R.id.icon_heart).setOnClickListener {

            navController?.navigate(R.id.espinillerasFragment)
        }

        findViewById<View>(R.id.icon_perfil).setOnClickListener {

            navController?.navigate(R.id.perfilFragment)
        }
    }

    private fun performSearch(query: String) {

        Toast.makeText(this, "Searching: $query", Toast.LENGTH_SHORT).show()


        val args = Bundle()
        args.putString("query", query)
        navController?.navigate(R.id.productosFragment, args)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController?.let { NavigationUI.navigateUp(it, drawerLayout) } == true
                || super.onSupportNavigateUp()
    }
}