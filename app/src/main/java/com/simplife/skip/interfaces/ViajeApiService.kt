package com.simplife.skip.interfaces

import com.simplife.skip.models.PasajeroEnLista
import com.simplife.skip.models.Viaje
import com.simplife.skip.models.ViajeInicio
import com.simplife.skip.models.ViajeRequest
import retrofit2.Call
import retrofit2.http.*

interface ViajeApiService {

    @GET("api/auth/skip/viajes/inicio")
    fun getHomeViajes(): Call<List<ViajeInicio>>

    @GET("api/auth/skip/viajes/inicio/{id}")
    fun getHomeViajesPorId(@Path("id") id: Long): Call<ViajeInicio>

    @POST("api/auth/skip/viajes/publicacion")
    fun publicarViaje(@Body viajeRequest: ViajeRequest): Call<Viaje>

    @GET("api/auth/skip/viajes/conductor/{conductorid}")
    fun getViajesDeConductor(@Path("conductorid") conductorid: Long): Call<List<ViajeInicio>>

    @GET("api/auth/skip/viajes/pasajero/{pasajeroid}")
    fun getViajesDePasajero(@Path("pasajeroid") pasajeroid: Long): Call<List<ViajeInicio>>

    @GET("api/auth/skip/viajes/{viajeId}/pasajeros")
    fun getPasajerosPorViaje(@Path("viajeId") viajeId: Long): Call<List<PasajeroEnLista>>

    @PUT("api/auth/skip/viajes/actualizar/{viajeId}")
    fun actualizarEstadoViaje(@Path("viajeId") viajeId: Long, @Query("estado") estado: String): Call<Int>
}