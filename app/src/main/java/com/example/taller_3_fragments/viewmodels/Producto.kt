package com.example.taller_3_fragments.viewmodels

data class Producto(
    val id: String, // O Int, según tu necesidad
    val titulo: String,
    val precio: Double, // O String si quieres formatearlo directamente
    val imagenUrl: String? = null, // Para cargar desde internet con Glide/Picasso
    val imagenResId: Int? = null // Para cargar desde @drawable (usa uno u otro)
    // Puedes añadir más campos como: descripción, categoría, etc.
)