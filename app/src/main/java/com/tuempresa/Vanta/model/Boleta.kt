package com.tuempresa.Vanta.model
import java.util.Date

open class Boleta(
    val id: Int,
    val cliente: Cliente,
    val vendedor: Persona,
    val productos: List<Producto>,
    val fecha: Date = Date()
) : Imprimible {

    fun calcularTotal(): Double {
        return productos.sumOf { it.precio }
    }

    override fun imprimir(): String {
        var detalleProductos = ""
        for (item in productos) {
            detalleProductos += "| ${item.nombre} | $${item.precio.toInt()} |\n"
        }

        return """
            BOLETA NRO: $id
            FECHA: $fecha
            --------------------------------
            CLIENTE: ${cliente.nombre} ${cliente.apellido}
            RUT: ${cliente.rut}
            DIRECCIÓN: ${cliente.direccion}
            --------------------------------
            ATENDIDO POR: ${vendedor.nombre} ${vendedor.apellido}
            --------------------------------
            PRODUCTOS:
            $detalleProductos
            --------------------------------
            TOTAL: $${calcularTotal().toInt()}
        """.trimIndent()
    }
}