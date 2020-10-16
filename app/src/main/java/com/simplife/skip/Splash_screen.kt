package com.simplife.skip

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simplife.skip.activities.Login
import com.simplife.skip.activities.MainActivity
import com.simplife.skip.interfaces.UsuarioApiService
import com.simplife.skip.models.LoginEntity
import com.simplife.skip.models.LoginRequest
import com.simplife.skip.util.URL_API
import kotlinx.android.synthetic.main.splash_screen.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log


class Splash_screen : AppCompatActivity() {


    private lateinit var userService: UsuarioApiService

    private lateinit var prefs : SharedPreferences
    private lateinit var edit: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        prefs = getSharedPreferences("user", Context.MODE_PRIVATE)
        edit= prefs.edit()


        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        userService = retrofit.create(UsuarioApiService::class.java)

        iv_note.alpha =0f

        iv_note.animate().setDuration(700).alpha(1f).withEndAction(){

            if (prefs.getString("codigo","") == "")
            {
                val i = Intent(this, Login::class.java)
                startActivity(i)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
            else
            {
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                login(prefs.getString("codigo","")!!, prefs.getString("contrasena","")!!)
            }
        }
    }



    fun login(email: String, pass: String){

        val loginRequest = LoginRequest(email,pass)

        userService.login(loginRequest).enqueue(object : Callback<LoginEntity> {
            override fun onResponse(call: Call<LoginEntity>?, response: Response<LoginEntity>?) {

                var usuario = response?.body()
                Log.i("Usuario", usuario.toString())
                if (usuario != null) {
                    edit.putString("token",usuario.token)
                    edit.putLong("idcuenta",usuario.cuentaid)
                    edit.putLong("idusuario",usuario.usuarioId)
                    edit.putString("rol", usuario.roles[0].toString())
                    edit.commit()

                    val i = Intent(applicationContext, MainActivity::class.java)
                    startActivity(i)
                    finish()
                }
                else{
                    val i = Intent(this@Splash_screen, Login::class.java)
                    startActivity(i)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                    Toast.makeText(this@Splash_screen,"Vuelva a ingresar sus credenciales", Toast.LENGTH_SHORT).show();
                }
            }

            override fun onFailure(call: Call<LoginEntity>?, t: Throwable?) {
                t?.printStackTrace()
                val i = Intent(this@Splash_screen, Login::class.java)
                startActivity(i)
                finish()
                Toast.makeText(this@Splash_screen,"Vuelva a ingresar sus credenciales", Toast.LENGTH_SHORT).show();
            }
        })

    }
}
