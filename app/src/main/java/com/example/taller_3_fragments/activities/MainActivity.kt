package com.example.taller_3_fragments.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
// import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
// import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.taller_3_fragments.R
import com.example.taller_3_fragments.utils.UserProfileManager
import com.example.taller_3_fragments.viewmodels.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import android.view.Menu // Necesario para acceder a los ítems del menú
import android.view.MenuItem // Necesario para acceder a los ítems del menú
import androidx.core.view.isVisible
import androidx.lifecycle.Observer // Si usas LiveData directamente

class MainActivity : AppCompatActivity() {

    // --- Vistas de navegación ---
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    // --- ViewModel y perfil del usuario ---
    private lateinit var userViewModel: UserViewModel
    private lateinit var iconPerfilImageView: ImageView

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- Inicializar Google Sign In Client ---
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // --- Inicializar vistas ---
        initViews() // iconPerfilImageView, drawerLayout, navigationView, bottomNavigationView

        // --- Inicializar ViewModel ---
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // --- Obtener datos del intent (desde LoginActivity) ---
        val email = intent.getStringExtra("email")
        val name = intent.getStringExtra("name")
        val photoUrlString = intent.getStringExtra("photoUrl")

        if (email != null || name != null || photoUrlString != null) {
            userViewModel.setUserProfile(email, name, photoUrlString)
        }

        observeUserProfile()

        // --- Configurar Toolbar ---
        setupToolbar()

        // --- Configurar NavController ---
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // --- Configurar AppBarConfiguration ---
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.inicioFragment, R.id.productosFragment, R.id.mapaFragment,
                R.id.carritoFragment,
                R.id.perfilFragment
                // todos los IDs de fragmentos que son destinos principales
                // accesibles desde el drawer o el bottom nav.
            ), drawerLayout
        )

        // Configura la ActionBar/Toolbar para que funcione con el NavController y el DrawerLayout
        setupActionBarWithNavController(navController, appBarConfiguration)

        // --- Configurar Navigation Drawer (NavigationView) ---
        setupNavigationDrawer()


        setupBottomNavigation()

        // --- Configurar buscador ---
        setupSearch()

        // --- Configurar clic en iconos de la Toolbar ---
        setupClickListeners()

        updateMenuItemsVisibility(userViewModel.userProfile.value != null)
    }

    /**
     * Actualiza la visibilidad de los ítems del menú "Login" y "Logout"
     * basado en el estado de inicio de sesión.
     */
    private fun updateMenuItemsVisibility(isLoggedIn: Boolean) {
        // Asegúrate que navigationView y su menú estén disponibles
        if (!::navigationView.isInitialized) return

        val menu: Menu = navigationView.menu
        val loginItem: MenuItem? = menu.findItem(R.id.loginMenu)
        val logoutItem: MenuItem? = menu.findItem(R.id.logoutMenu)

        loginItem?.isVisible = !isLoggedIn  // Mostrar "Login" si NO está logueado
        logoutItem?.isVisible = isLoggedIn   // Mostrar "Logout" si SÍ está logueado
    }

    private fun cerrarSesion() {
        googleSignInClient.signOut().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Sesión de Google cerrada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al cerrar sesión de Google", Toast.LENGTH_SHORT).show()
            }

            userViewModel.clearUserProfile()
            UserProfileManager.clearUserProfile(this)
            Toast.makeText(this, "Datos locales borrados", Toast.LENGTH_SHORT).show()

            // Cerrar el drawer si está abierto
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }

            try {
                // Navegar a inicioFragment (o LoginFragment) y limpiar el backstack
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(navController.graph.startDestinationId, true) // Pop hasta el inicio del grafo
                    .build()
                // Usamos this.navController que ya está inicializado correctamente
                this.navController.navigate(R.id.inicioFragment, null, navOptions)
            } catch (e: Exception) { // Captura excepciones más generales para robustez
                Toast.makeText(this, "Error de navegación al cerrar sesión: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun initViews() {
        iconPerfilImageView = findViewById(R.id.icon_perfil)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
    }

    private fun observeUserProfile() {
        userViewModel.userProfile.observe(this) { userProfile ->
            if (userProfile?.photoUrl != null) {
                Glide.with(this)
                    .load(userProfile.photoUrl)
                    .placeholder(R.drawable.imguser)
                    .error(R.drawable.imguser)
                    .circleCrop()
                    .into(iconPerfilImageView)
            } else {
                iconPerfilImageView.setImageResource(R.drawable.imguser)
            }
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    /**
     * Configura el Navigation Drawer (NavigationView) para manejar tanto la navegación
     * como acciones personalizadas como "Cerrar Sesión".
     */
    private fun setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Primero, intenta que NavigationUI maneje la navegación para ítems que son destinos
            val handledByNavigationUI = NavigationUI.onNavDestinationSelected(menuItem, navController)

            if (handledByNavigationUI) {
                // La navegación fue manejada por NavigationUI
            } else {
                if (menuItem.itemId == R.id.logoutMenu) {
                    cerrarSesion()
                } else if (menuItem.itemId == R.id.loginMenu) {
                    login()
                    // Puedes manejar otros ítems especiales aquí si es necesario
                    // o simplemente no hacer nada si no es un destino de navegación ni logoutMenu
                }
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true // Indica que el evento fue consumido
        }
    }

    private fun login() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


    /** Configura el menú inferior de navegación */
    private fun setupBottomNavigation() {

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.inicioFragment, R.id.carritoFragment, R.id.perfilFragment -> {
                    NavigationUI.onNavDestinationSelected(item, navController)
                    true
                }
                R.id.navigation_menu -> {
                    drawerLayout.openDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }
    }


    private fun setupSearch() {
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchEditText.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun setupClickListeners() {
        findViewById<View>(R.id.icon_heart).setOnClickListener {
            navController.navigate(R.id.mapaFragment)
        }

        findViewById<View>(R.id.icon_perfil).setOnClickListener {
            navController.navigate(R.id.perfilFragment)
        }
    }

    private fun performSearch(query: String) {
        Toast.makeText(this, "Searching: $query", Toast.LENGTH_SHORT).show()
        val args = Bundle().apply { putString("query", query) }
        navController.navigate(R.id.productosFragment, args)
    }

    /** Soporte para navegación cuando se presiona "Atrás" en el toolbar */
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    /**
     * Maneja el botón físico "Atrás" del dispositivo.
     * Si el drawer está abierto, lo cierra. Si no, permite el comportamiento normal.
     */
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}