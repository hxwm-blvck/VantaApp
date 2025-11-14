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

    // mutable actualiza sola la lista al cambiar algo
    val listaProductos = mutableStateListOf<Producto>(
        Electronico(1, "Audífonos Sony", 45000.0, 10, "Sony"),
        Electronico(2, "Mouse Gamer", 25000.0, 5, "Logitech"),
        Ropa(3, "Polera Vanta", 15000.0, 20, "M", "Algodón"),
        Ropa(4, "Jeans Básicos", 29990.0, 8, "42", "Mezclilla")
    )

    fun comprarProducto(producto: Producto) {

    }
    // -------------------------------------

    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri
    fun setPhotoUri(uri: Uri) { _photoUri.value = uri }

    // GPS
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

    // Bluetooth
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

    var tipoUsuarioActual = MutableStateFlow("Cliente")

    fun cambiarRol(nuevoRol: String) {
        tipoUsuarioActual.value = nuevoRol
    }
}