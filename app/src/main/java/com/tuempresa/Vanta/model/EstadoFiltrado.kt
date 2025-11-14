package com.tuempresa.Vanta.model

sealed class EstadoFiltrado { //estados cerrados
    object Filtrando : EstadoFiltrado()

    data class FiltradoExitoso(val producto: Producto) : EstadoFiltrado()

    data class FiltradoFallido(val error: String) : EstadoFiltrado()
}