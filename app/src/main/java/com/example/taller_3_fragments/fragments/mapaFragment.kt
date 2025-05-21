package com.example.taller_3_fragments.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment // Importante: Usar el Fragment de AndroidX
import com.example.taller_3_fragments.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class mapaFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var mapFragment: SupportMapFragment? = null


    private val aV68 = LatLng(4.675824, -74.087526)
    private val ccGranEstacion = LatLng(4.648107, -74.101411)
    private val ccTunal = LatLng(4.578085, -74.130318)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mapa, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener el SupportMapFragment y registrar el callback
        // Usar childFragmentManager para fragmentos dentro de fragmentos
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction().replace(R.id.map, mapFragment!!).commit() // Necesitarías un FrameLayout con este ID en tu XML
        }
        mapFragment?.getMapAsync(this) // Llamar a getMapAsync en la instancia correcta

        setupButtons(view) // Pasar la vista para encontrar los botones
    }

    // onMapReady es el callback que se llama cuando el mapa está listo para usarse
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Puedes configurar el mapa aquí si es necesario al inicio
        // Por ejemplo, mover la cámara a una ubicación por defecto o añadir marcadores iniciales
        // moveToLocation(aV68, "Sede Av. 68") // Opcional: mover a una ubicación inicial
    }

    private fun setupButtons(view: View) { // Recibir la vista del fragmento
        view.findViewById<Button>(R.id.ubi_tienda1)?.setOnClickListener {
            if (::mMap.isInitialized) { // Comprobar si mMap está inicializada
                moveToLocation(aV68, "Sede Av. 68")
            }
        }
        view.findViewById<Button>(R.id.ubi_tienda2)?.setOnClickListener {
            if (::mMap.isInitialized) {
                moveToLocation(ccGranEstacion, "Sede CC Gran Estación")
            }
        }
        view.findViewById<Button>(R.id.ubi_tienda3)?.setOnClickListener {
            if (::mMap.isInitialized) {
                moveToLocation(ccTunal, "Sede CC Tunal")
            }
        }
    }

    private fun moveToLocation(location: LatLng, title: String) {
        // Asegúrate de que mMap esté inicializada antes de usarla
        if (!::mMap.isInitialized) return

        mMap.clear()
        mMap.addMarker(MarkerOptions().position(location).title(title))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    // Opcional: Limpiar el mapa cuando la vista del fragmento es destruida
    override fun onDestroyView() {
        super.onDestroyView()
        // Importante para evitar memory leaks con el SupportMapFragment
        if (mapFragment != null) {
            // Una forma de limpiar, aunque el manejo moderno puede ser diferente
            // Si el SupportMapFragment está en el XML del fragmento,
            // el sistema a menudo lo maneja bien.
            // Considera removerlo explícitamente si lo añades programáticamente y no está en el backstack.
            // childFragmentManager.beginTransaction().remove(mapFragment!!).commitAllowingStateLoss()
            // mapFragment = null // Para estar seguros
        }
        // mMap = null // No es estrictamente necesario si el fragmento se destruye completamente
    }
}