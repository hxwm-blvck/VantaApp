package com.tuempresa.Vanta.model


class BoletaElectronica(
    id: Int,
    cliente: Cliente,
    vendedor: Persona,
    productos: List<Producto>,
    val correoCopia: String,
    val codigoAutorizacion: String
) : Boleta(id, cliente, vendedor, productos) {

    override fun imprimir(): String {
        val textoBase = super.imprimir()

        return """
            $textoBase
            --------------------------------
            DATOS ELECTRÓNICOS:
            Enviar copia a: $correoCopia
            Cód. Autorización: $codigoAutorizacion
            ================================
        """.trimIndent()
    }
}