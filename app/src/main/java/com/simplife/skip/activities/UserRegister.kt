package com.simplife.skip.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.simplife.skip.R
import com.simplife.skip.fragments.RolSelectionFragment
import com.simplife.skip.interfaces.UsuarioApiService
import com.simplife.skip.util.ApiClient
import com.simplife.skip.util.URL_API
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserRegister : AppCompatActivity() {

    private lateinit var userService: UsuarioApiService


    private lateinit var prefs : SharedPreferences
    private lateinit var edit: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)

        prefs = getSharedPreferences("user", Context.MODE_PRIVATE)
        edit= prefs.edit()


        userService = ApiClient.retrofit.create(UsuarioApiService::class.java)

        loadFragment(RolSelectionFragment())

        val backButtonRegister = findViewById<ImageButton>(R.id.back_button_register)
        backButtonRegister.setOnClickListener{
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun loadFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().also{
                fragmentTransaction -> fragmentTransaction.replace(R.id.fragment_container_register, fragment)
            fragmentTransaction.commit()
        }
    }
}