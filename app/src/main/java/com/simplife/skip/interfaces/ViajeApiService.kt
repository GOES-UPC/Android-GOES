package com.simplife.skip.interfaces

import com.simplife.skip.models.PasajeroEnLista
import com.simplife.skip.models.Viaje
import com.simplife.skip.models.ViajeInicio
import com.simplife.skip.models.ViajeRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ViajeApiService {

    @GET("api/auth/skip/viajes")
    fun getViajes(): Call<List<Viaje>>

    @GET("api/auth/skip/viajes/inicio")
    fun getHomeViajes(): Call<List<ViajeInicio>>

    @GET("api/auth/skip/viajes/{id}")
    fun getViajeById(@Path("id") id: Long): Call<Viaje>


    @POST("api/auth/skip/viajes/publicacion")
    fun publicarViaje(@Body viajeRequest: ViajeRequest): Call<Viaje>


    @GET("api/auth/skip/viajes/conductor/{conductorid}")
    fun getViajesDeConductor(@Path("conductorid") conductorid: Long): Call<List<Viaje>>

    @GET("api/auth/skip/viajes/{viajeId}/pasajeros")
    fun getPasajerosPorViaje(@Path("viajeId") viajeId: Long): Call<List<PasajeroEnLista>>
}