package com.tuempresa.Vanta.model

open class Persona(
    val rut: String,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val contrasena: String,
    val username: String
) {
    open fun registro(): String = correo
}

class Cliente(
    rut: String,
    nombre: String,
    apellido: String,
    correo: String,
    contrasena: String,
    username: String,
    val direccion: String
) : Persona(rut, nombre, apellido, correo, contrasena, username)

class Administrador(
    rut: String,
    nombre: String,
    apellido: String,
    correo: String,
    contrasena: String,
    username: String,
    val cargo: String,
    val reporte: String
) : Persona(rut, nombre, apellido, correo, contrasena, username)

class Repartidor(
    rut: String,
    nombre: String,
    apellido: String,
    correo: String,
    contrasena: String,
    username: String,
    val cargo: String,
    val seguimiento: String
) : Persona(rut, nombre, apellido, correo, contrasena, username)

class Supervisor(
    rut: String,
    nombre: String,
    apellido: String,
    correo: String,
    contrasena: String,
    username: String,
    val cargo: String,
    val informe: String
) : Persona(rut, nombre, apellido, correo, contrasena, username)