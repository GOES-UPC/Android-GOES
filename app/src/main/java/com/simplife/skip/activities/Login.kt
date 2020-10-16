package com.simplife.skip.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.emmanuelkehinde.shutdown.Shutdown
import com.google.gson.Gson
import com.simplife.skip.*
import com.simplife.skip.interfaces.UsuarioApiService
import com.simplife.skip.models.LoginEntity
import com.simplife.skip.models.LoginRequest
import com.simplife.skip.models.Usuario
import com.simplife.skip.util.URL_API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Login : AppCompatActivity() {

    private lateinit var loginBtn: Button
    private lateinit var userEmail: EditText
    private lateinit var userPass: EditText

    private lateinit var userService: UsuarioApiService
    private lateinit var homeP : Intent
    private lateinit var registerBtn: Button
    private val TAG = "Bryan"

    private lateinit var prefs :SharedPreferences
    private lateinit var edit:SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        prefs = getSharedPreferences("user", Context.MODE_PRIVATE)
        edit= prefs.edit()


        loginBtn = findViewById(R.id.login_button)
        userEmail = findViewById(R.id.user_email)
        userPass = findViewById(R.id.user_pass)
        registerBtn = findViewById(R.id.register_button)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        userService = retrofit.create(UsuarioApiService::class.java)

        registerBtn.setOnClickListener {
            val intent = Intent(this, UserRegister::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener{
            val loginRequest = LoginRequest(userEmail.text.toString(), userPass.text.toString())
            Log.i("Usuario", loginRequest.toString())

            userService.login(loginRequest).enqueue(object : Callback<LoginEntity> {
                override fun onResponse(call: Call<LoginEntity>?, response: Response<LoginEntity>?) {

                    var usuario = response?.body()
                    Log.i("Usuario", usuario.toString())
                    if (usuario != null) {
                        edit.putString("token",usuario.token)
                        edit.putLong("idcuenta",usuario.cuentaid)
                        edit.putLong("idusuario",usuario.usuarioId)
                        edit.putString("codigo", userEmail.text.toString())
                        edit.putString("contrasena", userPass.text.toString())
                        edit.putString("rol", usuario.roles[0].toString())
                        edit.commit()
                        Log.i("Viajes",usuario.toString())

                        homeP = Intent(applicationContext, MainActivity::class.java)
                        startActivity(homeP)
                        finish()
                    }
                    else{
                        Toast.makeText(this@Login,"Ingrese un correo y contraseña existente",Toast.LENGTH_SHORT).show();}
                }

                override fun onFailure(call: Call<LoginEntity>?, t: Throwable?) {
                    t?.printStackTrace()
                }
            })

        }

    }

    override fun onBackPressed() {
        Shutdown.now(this, "Presione de nuevo para salir")
    }

}
