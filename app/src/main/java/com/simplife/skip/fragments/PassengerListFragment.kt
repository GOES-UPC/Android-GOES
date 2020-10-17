package com.simplife.skip.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simplife.skip.R
import com.simplife.skip.adapter.MisViajeCondRecyclerAdapter
import com.simplife.skip.adapter.PasajerosRecyclerAdapter
import com.simplife.skip.interfaces.ViajeApiService
import com.simplife.skip.models.PasajeroEnLista
import com.simplife.skip.models.Viaje
import com.simplife.skip.models.ViajeInicio
import com.simplife.skip.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PassengerListFragment : Fragment() {

    lateinit var recyclerPassenger: RecyclerView
    lateinit var adapterPassengers: PasajerosRecyclerAdapter
    lateinit var viaje: ViajeInicio
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_passenger_list, container, false)
        val dialogBuilder = AlertDialog.Builder(requireContext())
        recyclerPassenger = view.findViewById(R.id.recycler_pasajeros)
        recyclerPassenger.layoutManager = LinearLayoutManager(context)
        adapterPassengers = PasajerosRecyclerAdapter()
        recyclerPassenger.adapter = adapterPassengers

        viaje = ViajeInicio()
        arguments?.let{ b ->
            viaje = b.getSerializable("viaje") as ViajeInicio
        }

        val origen: TextView = view.findViewById(R.id.startviaje_origen)
        val destino: TextView = view.findViewById(R.id.startviaje_destino)
        val horaInicio: TextView = view.findViewById(R.id.startviaje_horaorigen)
        val horaFin : TextView = view.findViewById(R.id.startviaje_horadestino)
        origen.text = viaje.paradas.get(0).ubicacion
        destino.text = viaje.paradas.get(1).ubicacion
        horaInicio.text = viaje.horaInicio
        horaFin.text = viaje.horaFin

        Log.i("PassengerListFragment", "Viaje id: ${viaje.id}")
        val startViajeButton = view.findViewById<Button>(R.id.startviaje_btn)
        val viajeService = ApiClient.retrofit.create(ViajeApiService::class.java)

        viajeService.getPasajerosPorViaje(viaje.id).enqueue(object: Callback<List<PasajeroEnLista>>{
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
            cargarDialogoIniciarViaje(viaje.id)
        }


        return view
    }

    fun loadFragment(fragment: Fragment){
        requireActivity().supportFragmentManager.beginTransaction().also{
                fragmentTransaction ->
            fragmentTransaction.replace(R.id.fragment_container_en_viaje, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    fun onBackPressed(){

    }

    fun cargarDialogoIniciarViaje(viajeId: Long){
        val dialog: AlertDialog


        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_start_viaje, null)

        val confirmButton = view.findViewById<Button>(R.id.boton_aceptar_start_viaje)
        val cancelButton = view.findViewById<Button>(R.id.boton_cancelar_start_viaje)

        builder.setView(view)

        dialog = builder.create()

        confirmButton.setOnClickListener{
            iniciarViaje(viajeId, dialog)
        }

        cancelButton.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()


    }

    fun iniciarViaje(viajeId: Long, dialog: AlertDialog){
        val viajeService = ApiClient.retrofit.create(ViajeApiService::class.java)

        viajeService.actualizarEstadoViaje(viajeId, "EN CURSO").enqueue(object: Callback<Int>{
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if(response.isSuccessful){
                    if(response.body()!! == 1){
                        Toast.makeText(requireContext(), "El viaje está en curso", Toast.LENGTH_SHORT).show()
                        val enViajeFragment = EnViajeFragment()
                        val bundle = Bundle()
                        bundle.putSerializable("viaje", viaje)
                        enViajeFragment.arguments = bundle
                        loadFragment(enViajeFragment)
                        dialog.dismiss()

                    }
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }


}