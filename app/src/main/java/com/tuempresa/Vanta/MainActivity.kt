package com.tuempresa.Vanta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.tuempresa.Vanta.ui.screens.MainScreen // Asegúrate de importar esto
import com.tuempresa.Vanta.ui.theme.VantaTheme // Importamos TU tema nuevo
import com.tuempresa.Vanta.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    // Instanciar ViewModel
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VantaTheme {
                // Surface contenedor base que usa el color de fondo del tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}