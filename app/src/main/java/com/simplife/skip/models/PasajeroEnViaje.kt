package com.simplife.skip.models

import java.io.Serializable

data class PasajeroEnViaje(
    var pasajeroId: Int,
    var nombre: String,
    var puntoEncuentro: String,
    var estadoPasajero: String,
    var avatarImagen: String
): Serializable{

}


