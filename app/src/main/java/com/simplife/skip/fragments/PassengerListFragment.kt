package com.simplife.skip.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplife.skip.R
import com.simplife.skip.adapter.MisViajeCondRecyclerAdapter
import com.simplife.skip.adapter.PasajerosRecyclerAdapter
import com.simplife.skip.interfaces.ViajeApiService
import com.simplife.skip.models.PasajeroEnLista
import com.simplife.skip.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PassengerListFragment : Fragment() {

    lateinit var recyclerPassenger: RecyclerView
    lateinit var adapterPassengers: PasajerosRecyclerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_passenger_list, container, false)

        recyclerPassenger = view.findViewById(R.id.recycler_pasajeros)
        recyclerPassenger.layoutManager = LinearLayoutManager(context)
        adapterPassengers = PasajerosRecyclerAdapter()
        recyclerPassenger.adapter = adapterPassengers

        var viajeId = 0L
        arguments?.let{ b ->
            viajeId = b.getLong("viajeId")
        }
        Log.i("PassengerListFragment", "Viaje id: ${viajeId}")
        val startViajeButton = view.findViewById<Button>(R.id.startviaje_btn)
        val viajeService = ApiClient.retrofit.create(ViajeApiService::class.java)

        viajeService.getPasajerosPorViaje(viajeId).enqueue(object: Callback<List<PasajeroEnLista>>{
            override fun onFailure(call: Call<List<PasajeroEnLista>>, t: Throwable) {
                Log.i("Fallamos", "No se pudieron cargas los pasajeros en el Fragment Passenger List")
            }

            override fun onResponse(
                call: Call<List<PasajeroEnLista>>,
                response: Response<List<PasajeroEnLista>>
            ) {
                if(response.isSuccessful){
                    response.body()?.let{
                        Log.i("PassengerListFragment", "Pasajeros: ${it}")
                        adapterPassengers = PasajerosRecyclerAdapter()
                        recyclerPassenger.adapter = adapterPassengers
                        adapterPassengers.submitList(it)
                    }

                    //
                }
            }
        })

        startViajeButton.setOnClickListener {
            var enViajeFragment = EnViajeFragment()
            var bundle = Bundle()
            bundle.putLong("viajeId", viajeId)
            enViajeFragment.arguments = bundle
            loadFragment(enViajeFragment)
        }


        return view
    }

    fun loadFragment(fragment: Fragment){
        requireActivity().supportFragmentManager.beginTransaction().also{
                fragmentTransaction ->
            fragmentTransaction.replace(R.id.fragment_container_register, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }


}