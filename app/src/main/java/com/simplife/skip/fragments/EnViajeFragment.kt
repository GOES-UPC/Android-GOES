package com.simplife.skip.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplife.skip.R
import com.simplife.skip.adapter.PasajerosEnViajeRecyclerAdapter
import com.simplife.skip.interfaces.ViajeApiService
import com.simplife.skip.models.PasajeroEnLista
import com.simplife.skip.models.PasajeroEnViaje
import com.simplife.skip.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EnViajeFragment : Fragment() {

    lateinit var recyclerPasajerosEnViaje: RecyclerView
    lateinit var adapterPasajerosEnViaje: PasajerosEnViajeRecyclerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_en_viaje, container, false)

        var viajeId = 0L
        arguments?.let{
            viajeId = it.getLong("viajeId")
        }
        recyclerPasajerosEnViaje = view.findViewById(R.id.recycler_pasajeros_a_recoger)
        recyclerPasajerosEnViaje.layoutManager = LinearLayoutManager(context)

        adapterPasajerosEnViaje = PasajerosEnViajeRecyclerAdapter(requireContext())


        recyclerPasajerosEnViaje.adapter = adapterPasajerosEnViaje

        val viajeService = ApiClient.retrofit.create(ViajeApiService::class.java)

        viajeService.getPasajerosPorViaje(viajeId).enqueue(object: Callback<List<PasajeroEnLista>> {
            override fun onFailure(call: Call<List<PasajeroEnLista>>, t: Throwable) {
                Log.i("Fallamos", "No se pudieron cargas los pasajeros en el Fragment Passenger List")
            }

            override fun onResponse(
                call: Call<List<PasajeroEnLista>>,
                response: Response<List<PasajeroEnLista>>
            ) {
                if(response.isSuccessful){
                    adapterPasajerosEnViaje.submitList(response.body()!! as ArrayList)
                }
            }
        })


        return view
    }

}