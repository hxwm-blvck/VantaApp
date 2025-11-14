package com.tuempresa.Vanta.model

open class Producto( //clase padre
    open val id: Int,
    open val nombre: String,
    open val precio: Double,
    open val cantidad: Int
) {
    open fun mostrarDetalles(): String {
        return "$nombre - $$precio"
    }
}

class Electronico(
    id: Int,
    nombre: String,
    precio: Double,
    cantidad: Int,
    val marca: String,
) : Producto(id, nombre, precio, cantidad) {

    override fun mostrarDetalles(): String {
        return super.mostrarDetalles() + " (Marca: $marca)"
    }
}

class Ropa(
    id: Int,
    nombre: String,
    precio: Double,
    cantidad: Int,
    val talla: String,
    val material: String
) : Producto(id, nombre, precio, cantidad) {

    override fun mostrarDetalles(): String {
        return super.mostrarDetalles() + " (Talla: $talla, Material: $material)"
    }
}