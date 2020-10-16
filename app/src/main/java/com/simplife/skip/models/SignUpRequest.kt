package com.simplife.skip.models


import java.io.Serializable


data class SignUpRequest(

    var codigo: String,
    var contrasena: String,
    var dni: String,
    var nombres: String,
    var apellidos: String,
    var sede: String,
    var imagen: String,
    var role: Set<String>,
    var infoConductor: InformacionConductor?,
    var auto: Auto?

):Serializable{
    /*constructor(codigo: String,
                contrasena: String,
                dni: String,
                nombres: String,
                apellidos: String,
                sede: String,
                imagen: String,
                role: Set<String>,
                infoConductor: InformacionConductor,
                auto: Auto): this(codigo, contrasena, dni, nombres, apellidos, sede, imagen, role)*/



}