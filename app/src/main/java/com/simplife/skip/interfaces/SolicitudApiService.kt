package com.simplife.skip.interfaces

import com.simplife.skip.models.Solicitud
import com.simplife.skip.models.SolicitudRequest
import com.simplife.skip.models.Viaje
import com.simplife.skip.models.ViajeRequest
import retrofit2.Call
import retrofit2.http.*

interface SolicitudApiService {

    @GET("api/auth/skip/solicitudes/usuario/{id}")
    fun getSolicitudesPorUsuario(@Path("id") id: Long): Call<List<Solicitud>>


    @GET("api/auth/skip/solicitudes/conductor/{id}")
    fun getSolicitudesPorConductor(@Path("id") id: Long): Call<List<Solicitud>>

    @POST("api/auth/skip/solicitudes")
    fun solicitarViaje(@Body solicitudRequest: SolicitudRequest): Call<Solicitud>

    @PUT("api/auth/skip/solicitudes")
    fun actualizarEstadoPasajero(@Query("viajeId") viajeId: Long,
                                 @Query("pasajeroId") pasajeroId: Long,
                                 @Query("estado") estado: String): Call<Int>

}