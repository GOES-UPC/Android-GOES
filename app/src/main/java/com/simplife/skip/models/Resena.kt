package com.simplife.skip.models

import java.io.Serializable

data class Resena (
    var body: String,
    var image: String,
    var username: String,
    var publish: String,
    var valoracion: Double
) :Serializable{
    override fun toString(): String {
        return "Resena(body='$body', image='$image', username='$username', publish='$publish', valoracion=$valoracion)"
    }
}