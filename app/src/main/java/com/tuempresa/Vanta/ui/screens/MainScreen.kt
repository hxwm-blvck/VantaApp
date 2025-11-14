package com.tuempresa.Vanta.ui.screens


import com.tuempresa.Vanta.R
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tuempresa.Vanta.viewmodel.MainViewModel
import com.tuempresa.Vanta.ui.componentes.CameraPreview


fun getBluetoothConnectPermission(): String {
    return try {
        Manifest.permission.BLUETOOTH_CONNECT
    } catch (e: Throwable) {
        "android.permission.BLUETOOTH_CONNECT"
    }
}

fun hasBluetoothConnectPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        getBluetoothConnectPermission()
    ) == PackageManager.PERMISSION_GRANTED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {

    var selectedItemIndex by remember { mutableStateOf(0) }
    val categorias = listOf("Tienda", "Listas", "Sensores", "Perfil")

    val photoUri by viewModel.photoUri.collectAsState()
    val location by viewModel.location.collectAsState()
    val bluetoothDevices by viewModel.bluetoothDevices.collectAsState()
    val context = LocalContext.current

    val cameraPermissionLauncher = rememberLauncherForActivityResult(RequestPermission()) { }
    val locationPermissionLauncher = rememberLauncherForActivityResult(RequestPermission()) { }
    val bluetoothScanLauncher = rememberLauncherForActivityResult(RequestPermission()) { }
    val bluetoothConnectLauncher = rememberLauncherForActivityResult(RequestPermission()) { }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Vanta") },
                actions = {

                    Image(
                        painter = painterResource(id = com.tuempresa.Vanta.R.drawable.logo),
                        contentDescription = "Logo App",
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .height(40.dp)
                            .width(40.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            )
        },

        bottomBar = {
            NavigationBar {
                categorias.forEachIndexed { index, texto ->
                    NavigationBarItem(
                        selected = (selectedItemIndex == index),
                        onClick = { selectedItemIndex = index },
                        label = { Text(texto) },
                        icon = {
                            when (index) {
                                0 -> Icon(Icons.Default.ShoppingCart, contentDescription = null)
                                1 -> Icon(Icons.Default.List, contentDescription = null)
                                2 -> Icon(Icons.Default.Home, contentDescription = null) // Aquí pondremos los sensores
                                3 -> Icon(Icons.Default.AccountCircle, contentDescription = null)
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        Box(modifier = Modifier.padding(innerPadding)) {

            when (selectedItemIndex) {

                0 -> {
                    val listaProductos = viewModel.listaProductos

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Catálogo Vanta",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(listaProductos) { producto ->
                                Card(
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = producto.nombre,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "$${producto.precio.toInt()}",
                                                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = producto.mostrarDetalles(),
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Button(
                                            onClick = { viewModel.comprarProducto(producto) },
                                            modifier = Modifier.align(Alignment.End)
                                        ) {
                                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Agregar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        item {
                            Text("Cámara", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                            Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                                Text("Permiso Cámara")
                            }
                            CameraPreview(onPhotoCaptured = { viewModel.setPhotoUri(it) })

                            photoUri?.let {
                                AsyncImage(
                                    model = it,
                                    contentDescription = "Foto capturada",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .padding(top = 8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        item {
                            Text("GPS", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                            Button(onClick = {
                                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                viewModel.startLocationUpdates()
                            }) {
                                Text("Obtener ubicación")
                            }
                            location?.let {
                                Text("Latitud: ${it.latitude}, Longitud: ${it.longitude}")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        item {
                            Text("Bluetooth", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                            Button(onClick = {
                                bluetoothScanLauncher.launch(Manifest.permission.BLUETOOTH_SCAN)
                                bluetoothConnectLauncher.launch(getBluetoothConnectPermission())
                                viewModel.startBluetoothDiscovery()
                            }) {
                                Text("Buscar dispositivos Bluetooth")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        items(bluetoothDevices) { device ->
                            val hasPermission = hasBluetoothConnectPermission(context)
                            var textShow = "Permiso BLUETOOTH_CONNECT requerido"
                            if (hasPermission) {
                                try {
                                    val name = device.name ?: "Sin nombre"
                                    val address = device.address
                                    textShow = "$name - $address"
                                } catch (e: SecurityException) {
                                    textShow = "Permiso denegado en ejecución"
                                }
                            }
                            Text(text = textShow)
                        }
                    }
                }
                3 -> {

                    val rolActual by viewModel.tipoUsuarioActual.collectAsState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Modo Actual:",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = rolActual.uppercase(),
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Text("Cambiar tipo de usuario:", style = MaterialTheme.typography.titleMedium)

                        Spacer(modifier = Modifier.height(16.dp))

                        val roles = listOf("Cliente", "Administrador", "Supervisor", "Repartidor")

                        roles.forEach { rol ->
                            Button(
                                onClick = { viewModel.cambiarRol(rol) },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                // Cambiamos color si es el rol seleccionado
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (rolActual == rol) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Entrar como $rol")
                            }
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Sección en construcción: ${categorias[selectedItemIndex]}")
                    }
                }
            }
        }
    }
}