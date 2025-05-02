package com.example.taller_3_fragments.activities

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.taller_3_fragments.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


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