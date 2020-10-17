package com.simplife.skip.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplife.skip.R
import com.simplife.skip.adapter.PasajerosRecyclerAdapter
import com.simplife.skip.adapter.ViajeRecyclerAdapter
import com.simplife.skip.fragments.EnViajeFragment
import com.simplife.skip.fragments.PassengerListFragment
import com.simplife.skip.models.Viaje
import com.simplife.skip.models.ViajeInicio
import kotlinx.android.synthetic.main.activity_start_viaje.*
import kotlinx.android.synthetic.main.activity_viaje_detail.*

class StartViajeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_viaje)

        val viaje = intent.getSerializableExtra("via") as ViajeInicio
        val passengerList = PassengerListFragment()
        val enViajeFragment = EnViajeFragment()
        val bundle = Bundle()
        bundle.putSerializable("viaje", viaje)
        when(viaje.estadoViaje){
            "PUBLICADO" -> {
                passengerList.arguments = bundle
                loadFragment(passengerList)
            }
            "EN CURSO" -> {
                enViajeFragment.arguments = bundle
                loadFragment(enViajeFragment)
            }
        }

        val backButton = findViewById<ImageButton>(R.id.viajeback_button)
        backButton.setOnClickListener{
            finish()
        }


    }

    fun loadFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().also{
                fragmentTransaction -> fragmentTransaction.replace(R.id.fragment_container_en_viaje, fragment)
            fragmentTransaction.commit()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
