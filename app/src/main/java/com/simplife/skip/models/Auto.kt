package com.simplife.skip.models

import java.io.Serializable

data class Auto(
    var placa: String,
    var polizaSoat: String,
    var marca: String,
    var modelo: String,
    var limitePersonas: Int,
    var anhosUso: Int
): Serializable {
    override fun toString(): String {
        return "Auto(placa='$placa', polizaSoat='$polizaSoat', marca='$marca', modelo='$modelo', limitePersonas=$limitePersonas, anhosUso=$anhosUso)"
    }
}