package com.simplife.skip.interfaces

import com.simplife.skip.models.ReportRequest
import com.simplife.skip.models.Reporte
import com.simplife.skip.models.Solicitud
import retrofit2.Call
import retrofit2.http.*

interface ReportApiService {
    @GET("api/auth/skip/reportes")
    fun getReportesporViaje(@Query("viajeId") id: Long): Call<List<Reporte>>

    @POST("api/auth/skip/reportes")
    fun calificarViaje(@Body reportRequest: ReportRequest, @Query("usuarioId") usuarioId: Long, @Query("viajeId") viajeId: Long): Call<Reporte>
}