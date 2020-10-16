package com.simplife.skip.interfaces

import com.simplife.skip.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UsuarioApiService {

    @POST("api/auth/usuario/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginEntity>

    @GET("api/auth/skip/usuarios/{id}")
    fun getUsuarioById(@Path("id") id: Long): Call<Usuario>

    @POST("api/auth/usuario/registro")
    fun registroUsuario(@Body request: SignUpRequest): Call<RegisterEntity>

}