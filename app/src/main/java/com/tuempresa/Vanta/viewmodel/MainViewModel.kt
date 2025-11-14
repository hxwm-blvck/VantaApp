package com.tuempresa.Vanta.viewmodel

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.mutableStateListOf
import com.tuempresa.Vanta.model.Electronico
import com.tuempresa.Vanta.model.Producto
import com.tuempresa.Vanta.model.Ropa

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val listaProductos = mutableStateListOf<Producto>(
        Electronico(1, "Audífonos Sony", 45000.0, 10, "Sony"),
        Electronico(2, "Mouse Gamer", 25000.0, 5, "Logitech"),
        Ropa(3, "Polera Básica Negra", 15000.0, 20, "M", "Algodón"),
        Ropa(4, "Jeans Básicos Gris", 29990.0, 8, "42", "Mezclilla"),
        Electronico(5, "Teclado Razer BlackWidow", 89990.0, 7, "Razer"),
        Electronico(6, "Mouse Razer DeathAdder", 45990.0, 12, "Razer"),
        Electronico(7, "Monitor Samsung 24\"", 120000.0, 4, "Samsung"),
        Electronico(8, "Parlante JBL Flip 6", 79990.0, 6, "JBL"),
        Electronico(9, "Smartwatch Xiaomi Band", 39990.0, 25, "Xiaomi"),
        Ropa(10, "Jeans Baggy Azul Claro", 32990.0, 15, "40", "Denim"),
        Ropa(11, "Pantalón Slim Fit Chino", 24990.0, 10, "42", "Gabardina"),
        Ropa(12, "Polera Oversize Beige", 18990.0, 12, "L", "Algodón Pesado"),
        Ropa(13, "Polerón Hoodie Negro", 35000.0, 8, "XL", "Poliéster"),
        Ropa(14, "Camisa Leñadora Roja", 22000.0, 9, "M", "Franela"),
        Electronico(15, "Cámara GoPro Hero", 250000.0, 3, "GoPro")
    )

    var textoBusqueda = MutableStateFlow("")

    fun actualizarBusqueda(nuevoTexto: String) {
        textoBusqueda.value = nuevoTexto
    }

    fun obtenerProductosFiltrados(): List<Producto> {
        val texto = textoBusqueda.value
        return if (texto.isEmpty()) {
            listaProductos
        } else {
            listaProductos.filter {
                it.nombre.contains(texto, ignoreCase = true)
            }
        }
    }
    var boletaGenerada = MutableStateFlow<String?>(null)

    val carritoCompras = mutableStateListOf<Producto>()

    fun calcularTotalCarrito(): Int {
        return carritoCompras.sumOf { it.precio.toInt() }
    }

    fun comprarProducto(producto: Producto) {
        if (producto.cantidad > 0) {
            carritoCompras.add(producto)
        }
    }

    fun borrarDelCarrito(producto: Producto) {
        carritoCompras.remove(producto)
    }

    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri
    fun setPhotoUri(uri: Uri) { _photoUri.value = uri }

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location

    @android.annotation.SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val context = getApplication<Application>()
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()
            fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    _location.value = result.lastLocation
                }
            }, Looper.getMainLooper())
        } else {
            Toast.makeText(context, "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show()
        }
    }

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val _bluetoothDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val bluetoothDevices: StateFlow<List<BluetoothDevice>> = _bluetoothDevices
    private var receiver: BroadcastReceiver? = null

    private fun hasBluetoothScanPermission(): Boolean {
        val context = getApplication<Application>()
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasBluetoothConnectPermission(): Boolean {
        val context = getApplication<Application>()
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
    }

    fun startBluetoothDiscovery() {
        val context = getApplication<Application>()
        if (!hasBluetoothScanPermission() || !hasBluetoothConnectPermission()) {
            Toast.makeText(context, "Permisos Bluetooth no concedidos (SCAN/CONNECT)", Toast.LENGTH_SHORT).show()
            return
        }
        bluetoothAdapter?.let { adapter ->
            if (!adapter.isEnabled) {
                Toast.makeText(context, "Bluetooth no está habilitado", Toast.LENGTH_SHORT).show()
                return
            }
            if (hasBluetoothScanPermission() && adapter.isDiscovering) {
                adapter.cancelDiscovery()
            }
            _bluetoothDevices.value = emptyList()
            if (receiver == null) {
                receiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (intent?.action == BluetoothDevice.ACTION_FOUND) {
                            if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                                device?.let { _bluetoothDevices.value = _bluetoothDevices.value + it }
                            }
                        }
                    }
                }
                context.registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
            }
            adapter.startDiscovery()
        } ?: run {
            Toast.makeText(context, "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCleared() {
        super.onCleared()
        val context = getApplication<Application>()
        receiver?.let {
            context.unregisterReceiver(it)
            receiver = null
        }
    }

    fun finalizarCompra() {
        if (carritoCompras.isNotEmpty()) {

            val clienteReal = com.tuempresa.Vanta.model.Cliente(
                rut = "11.111.111-1",
                nombre = "Juan",
                apellido = "Pérez",
                correo = "cliente@vanta.cl",
                contrasena = "pass123",
                username = "juan.perez",
                direccion = "Av. Siempre Viva 742"
            )

            val vendedorTurno = com.tuempresa.Vanta.model.Administrador(
                rut = "22.222.222-K",
                nombre = "Jocelyn",
                apellido = "Aguilera",
                correo = "admin@vanta.cl",
                contrasena = "admin123",
                username = "j.aguilera",
                cargo = "Gerente de Tienda",
                reporte = "Reporte-Diario-Ventas"
            )

            val nuevaBoleta = com.tuempresa.Vanta.model.BoletaElectronica(
                id = (1000..9999).random(),
                cliente = clienteReal,
                vendedor = vendedorTurno,
                productos = carritoCompras.toList(),
                correoCopia = clienteReal.correo,
                codigoAutorizacion = "AUTH-${System.currentTimeMillis().toString().takeLast(6)}"
            )

            boletaGenerada.value = nuevaBoleta.imprimir()

            carritoCompras.clear()
        }
    }


    var tipoUsuarioActual = MutableStateFlow("Cliente")

    fun cambiarRol(nuevoRol: String) {
        tipoUsuarioActual.value = nuevoRol
    }

    fun aumentarStock(producto: Producto) {
        val index = listaProductos.indexOf(producto)
        if (index != -1) {
            listaProductos[index] = listaProductos[index].apply { cantidad += 1 }
        }
    }

    private val listaClientesRegistrados = mutableListOf<com.tuempresa.Vanta.model.Cliente>()

    var mensajeRegistro = MutableStateFlow("")


    fun validarRut(rut: String): Boolean {
        val regex = Regex("^[0-9]{7,8}-[0-9kK]{1}$")
        return regex.matches(rut)
    }

    fun validarCorreo(correo: String): Boolean {
        return correo.endsWith("@gmail.com") || correo.endsWith("@duocuc.cl")
    }

    fun validarUsername(username: String): Boolean {
        return username.isNotBlank() && !username.contains(" ")
    }

    fun validarContrasena(contrasena: String): Boolean {
        return contrasena.length >= 6
    }

    fun camposCompletos(vararg campos: String): Boolean {
        return campos.all { it.isNotBlank() }
    }

    fun registrarCliente(
        rut: String,
        nombre: String,
        apellido: String,
        correo: String,
        contrasena: String,
        username: String,
        direccion: String
    ): Boolean {

        if (!camposCompletos(rut, nombre, apellido, correo, contrasena, username, direccion)) {
            mensajeRegistro.value = "Todos los campos son obligatorios"
            return false
        }

        if (!validarRut(rut)) {
            mensajeRegistro.value = "RUT inválido (ej: 12345678-9)"
            return false
        }

        if (!validarCorreo(correo)) {
            mensajeRegistro.value = "Correo debe ser @gmail.com o @duocuc.cl"
            return false
        }

        if (!validarUsername(username)) {
            mensajeRegistro.value = "Username no puede tener espacios"
            return false
        }

        if (!validarContrasena(contrasena)) {
            mensajeRegistro.value = "Contraseña muy corta (min 6)"
            return false
        }

        if (listaClientesRegistrados.any { it.rut == rut }) {
            mensajeRegistro.value = "Ya existe un cliente con ese RUT"
            return false
        }

        if (listaClientesRegistrados.any { it.username == username }) {
            mensajeRegistro.value = "El username ya está ocupado"
            return false
        }
        val nuevoCliente = com.tuempresa.Vanta.model.Cliente(
            rut, nombre, apellido, correo, contrasena, username, direccion
        )
        listaClientesRegistrados.add(nuevoCliente)

        mensajeRegistro.value = "Registro exitoso: ${nuevoCliente.nombre}"
        return true
    }
}