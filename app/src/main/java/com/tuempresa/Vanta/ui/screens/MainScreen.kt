package com.tuempresa.Vanta.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tuempresa.Vanta.viewmodel.MainViewModel
import com.tuempresa.Vanta.ui.componentes.CameraPreview
import com.tuempresa.Vanta.model.Producto
import androidx.compose.foundation.background

fun getBluetoothConnectPermission(): String {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        Manifest.permission.BLUETOOTH_CONNECT
    } else {
        Manifest.permission.BLUETOOTH
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
    val categorias = listOf("Tienda", "Carrito", "Sensores", "Perfil")

    var mostrandoRegistro by remember { mutableStateOf(false) }

    val photoUri by viewModel.photoUri.collectAsState()
    val location by viewModel.location.collectAsState()
    val bluetoothDevices by viewModel.bluetoothDevices.collectAsState()
    val context = LocalContext.current

    val cameraPermissionLauncher = rememberLauncherForActivityResult(RequestPermission()) { }
    val locationPermissionLauncher = rememberLauncherForActivityResult(RequestPermission()) { }
    val bluetoothScanLauncher = rememberLauncherForActivityResult(RequestPermission()) { }
    val bluetoothConnectLauncher = rememberLauncherForActivityResult(RequestPermission()) { }

    if (mostrandoRegistro) {
        PantallaRegistro(
            onRegistroCompletado = {
                mostrandoRegistro = false
                Toast.makeText(context, "Cliente registrado con éxito", Toast.LENGTH_SHORT).show()
            },
            onCancelar = { mostrandoRegistro = false }
        )
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Vanta", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background, // Ahora será NEGRA
                        titleContentColor = MaterialTheme.colorScheme.primary  // El título será ROSADO
                    ),
                    actions = {
                        Image(
                            painter = painterResource(id = com.tuempresa.Vanta.R.drawable.logo),
                            contentDescription = "Logo App",
                            modifier = Modifier.padding(end = 12.dp).size(40.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    categorias.forEachIndexed { index, texto ->
                        NavigationBarItem(
                            selected = (selectedItemIndex == index),
                            onClick = { selectedItemIndex = index },
                            label = { Text(texto) },
                            icon = {
                                when (index) {
                                    0 -> Icon(Icons.Default.ShoppingCart, contentDescription = null)
                                    1 -> Icon(Icons.Default.List, contentDescription = null)
                                    2 -> Icon(Icons.Default.Place, contentDescription = null)
                                    3 -> Icon(Icons.Default.Person, contentDescription = null)
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->

            Box(modifier = Modifier.padding(innerPadding)) {

                when (selectedItemIndex) {
                    0 -> {
                        val textoBusqueda by viewModel.textoBusqueda.collectAsState()
                        val productosVisibles = viewModel.obtenerProductosFiltrados()

                        var mostrarDetalle by remember { mutableStateOf(false) }
                        var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }

                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            OutlinedTextField(
                                value = textoBusqueda,
                                onValueChange = { viewModel.actualizarBusqueda(it) },
                                label = { Text("Buscar producto...") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(productosVisibles) { producto ->
                                    Card(
                                        elevation = CardDefaults.cardElevation(4.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            productoSeleccionado = producto
                                            mostrarDetalle = true
                                        },
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF1E1E1E)
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .padding(16.dp)
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.background, // Fondo negro para el icono
                                                modifier = Modifier.size(50.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.ShoppingCart,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary // Rosado Neón
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(16.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = producto.nombre,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White, // Texto blanco resalta en gris oscuro
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = producto.mostrarDetalles(),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.Gray // Descripción en gris para jerarquía
                                                )
                                            }

                                            Text(
                                                text = "$${producto.precio.toInt()}",
                                                color = MaterialTheme.colorScheme.primary, // Precio Rosado
                                                fontWeight = FontWeight.ExtraBold,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (mostrarDetalle && productoSeleccionado != null) {
                            AlertDialog(
                                onDismissRequest = { mostrarDetalle = false },
                                title = { Text(productoSeleccionado!!.nombre) },
                                text = {
                                    Column {
                                        Text("Precio: $${productoSeleccionado!!.precio.toInt()}")
                                        Text("Stock: ${productoSeleccionado!!.cantidad}", fontWeight = FontWeight.Bold)
                                        Text(productoSeleccionado!!.mostrarDetalles())
                                    }
                                },
                                confirmButton = {
                                    Button(onClick = {
                                        viewModel.comprarProducto(productoSeleccionado!!)
                                        mostrarDetalle = false
                                        Toast.makeText(context, "Agregado al carrito", Toast.LENGTH_SHORT).show()
                                    }) { Text("Agregar") }
                                },
                                dismissButton = { TextButton(onClick = { mostrarDetalle = false }) { Text("Cerrar") } }
                            )
                        }
                    }

                    1 -> {
                        val itemsCarrito = viewModel.carritoCompras
                        val total = viewModel.calcularTotalCarrito()
                        val boletaTexto by viewModel.boletaGenerada.collectAsState()

                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Text("Tu Carrito", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))

                            if (itemsCarrito.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                                        Text("El carrito está vacío", color = Color.Gray)
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(itemsCarrito) { prod ->
                                        Card {
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(prod.nombre, fontWeight = FontWeight.Bold)
                                                    Text("$${prod.precio.toInt()}")
                                                }
                                                // Botón para borrar item
                                                IconButton(onClick = { viewModel.borrarDelCarrito(prod) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red)
                                                }
                                            }
                                        }
                                    }
                                }

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Total a Pagar:", style = MaterialTheme.typography.titleLarge)
                                            Text("$$total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = { viewModel.finalizarCompra() },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Finalizar Compra")
                                        }
                                    }
                                }
                            }
                        }

                        if (boletaTexto != null) {
                            AlertDialog(
                                onDismissRequest = { viewModel.boletaGenerada.value = null }, // Cerrar manual
                                title = { Text("Compra Exitosa") },
                                text = {
                                    Box(modifier = Modifier.heightIn(max = 400.dp)) {
                                        Text(boletaTexto!!, style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                confirmButton = {
                                    Button(onClick = { viewModel.boletaGenerada.value = null }) { Text("Aceptar") }
                                }
                            )
                        }
                    }

                    2 -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            item {
                                Text(
                                    "Sensores",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.Black
                                )

                                Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                                    Text("Permiso Cámara")
                                }
                                CameraPreview(onPhotoCaptured = { viewModel.setPhotoUri(it) })
                                photoUri?.let {
                                    AsyncImage(model = it, contentDescription = null, modifier = Modifier.height(200.dp))
                                }
                            }
                            item {
                                Button(onClick = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION); viewModel.startLocationUpdates() }) {
                                    Text("Obtener ubicación")
                                }
                                location?.let {
                                    Text(
                                        "Lat: ${it.latitude}, Lon: ${it.longitude}",
                                        color = Color.Black
                                    )
                                }
                            }
                            item {
                                Button(onClick = { bluetoothScanLauncher.launch(Manifest.permission.BLUETOOTH_SCAN); viewModel.startBluetoothDiscovery() }) {
                                    Text("Buscar dispositivos Bluetooth")
                                }
                            }
                            items(bluetoothDevices) { device ->
                                Text(
                                    device.name ?: device.address,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    3 -> {
                        val rolActual by viewModel.tipoUsuarioActual.collectAsState()

                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Rol Actual: $rolActual", style = MaterialTheme.typography.headlineMedium)
                            Spacer(modifier = Modifier.height(32.dp))

                            Text("Cambiar Rol:")
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { viewModel.cambiarRol("Administrador") }) { Text("Admin") }
                                Button(onClick = { viewModel.cambiarRol("Cliente") }) { Text("Cliente") }
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = { mostrandoRegistro = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Registrar Nuevo Cliente")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaRegistro(onRegistroCompletado: () -> Unit, onCancelar: () -> Unit) {
    var rut by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Registro de Cliente",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary // Título en Rosado para destacar
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(value = rut, onValueChange = { rut = it }, label = { Text("RUT") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = onCancelar,
                modifier = Modifier.weight(1f),
                // Borde del botón cancelar en rosado para que se vea
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text("Cancelar", color = MaterialTheme.colorScheme.primary)
            }

            Button(
                onClick = { onRegistroCompletado() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Registrar", color = Color.White)
            }
        }
    }
}