package com.example.vanta2.Model

open class Persona(val rut:String, val nombre:String, val apellido: String, val correo: String,val contraseña: String,val username: String) {
    open fun registro(): String=correo
}