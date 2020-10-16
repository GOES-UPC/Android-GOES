package com.simplife.skip.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplife.skip.R
import com.simplife.skip.adapter.PasajerosRecyclerAdapter
import com.simplife.skip.adapter.ViajeRecyclerAdapter
import com.simplife.skip.fragments.EnViajeFragment
import com.simplife.skip.fragments.PassengerListFragment
import com.simplife.skip.models.Viaje
import kotlinx.android.synthetic.main.activity_start_viaje.*
import kotlinx.android.synthetic.main.activity_viaje_detail.*

class StartViajeActivity : AppCompatActivity() {

    private  lateinit var pasajeroAdapter: PasajerosRecyclerAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_viaje)

        val viaje = intent.getSerializableExtra("via") as Viaje
        val passengerList = PassengerListFragment()
        val fragmentEnViaje = EnViajeFragment()
        var bundle = Bundle()
        bundle.putLong("viajeId", viaje.id)
        passengerList.arguments = bundle
        loadFragment(passengerList)



        /*startviaje_origen.setText(viaje.conductor.ubicacion)
        startviaje_horaorigen.setText(viaje.horaInicio)
        startviaje_destino.setText(viaje.conductor.sede)
        startviaje_horadestino.setText(viaje.horaLlegada)*/

       /* recyclerView = findViewById(R.id.recycler_pasajeros)
        recyclerView.layoutManager = LinearLayoutManager(this)
        //val topSpacingDecoration = TopSpacingItemDecoration(30)
        //recyclerView.addItemDecoration(topSpacingDecoration)
        pasajeroAdapter = PasajerosRecyclerAdapter()
        recyclerView.adapter = pasajeroAdapter*/

    }

    fun loadFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().also{
                fragmentTransaction -> fragmentTransaction.replace(R.id.fragment_container_en_viaje, fragment)
            fragmentTransaction.commit()
        }
    }
}
