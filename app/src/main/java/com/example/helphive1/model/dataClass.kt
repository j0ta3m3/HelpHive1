package com.example.helphive1.model

data class Publication(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val remuneracion: Double = 0.0,
    val autorId: String = "",
    val localizacion : String = "",
    var estado : String = "",
    var idWorker : String = ""
) {
    constructor() : this("", "", "", "", 0.0, "","","","")
}


data class Usuario(
    val id: String?,
    val username : String,
    val nombre: String, val apellido1: String, val apellido2: String, val profilePictureUrl: String? , val descripcion: String,
    val email: String, val telefono: String, val localidad: String, val realizados: Int, val ofrecidos : Int)  {
    constructor() : this("", "", "", "", "","","","","","",0,0)
}
