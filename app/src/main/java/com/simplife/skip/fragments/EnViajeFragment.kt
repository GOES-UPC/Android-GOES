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
import com.simplife.skip.adapter.PasajerosEnViajeRecyclerAdapter
import com.simplife.skip.interfaces.SolicitudApiService
import com.simplife.skip.interfaces.ViajeApiService
import com.simplife.skip.models.PasajeroEnLista
import com.simplife.skip.models.ViajeInicio
import com.simplife.skip.util.ApiClient
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EnViajeFragment : Fragment() {

    lateinit var recyclerPasajerosEnViaje: RecyclerView
    lateinit var adapterPasajerosEnViaje: PasajerosEnViajeRecyclerAdapter
    val viajeService = ApiClient.retrofit.create(ViajeApiService::class.java)
    val solicitudService = ApiClient.retrofit.create(SolicitudApiService::class.java)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_en_viaje, container, false)
        val buttonFinalizar = view.findViewById<Button>(R.id.endviaje_btn)


        var viaje = ViajeInicio()
        arguments?.let{ b ->
            viaje = b.getSerializable("viaje") as ViajeInicio
        }
        recyclerPasajerosEnViaje = view.findViewById(R.id.recycler_pasajeros_a_recoger)
        recyclerPasajerosEnViaje.layoutManager = LinearLayoutManager(context)

        val origen: TextView = view.findViewById(R.id.startviaje_origen)
        val destino: TextView = view.findViewById(R.id.startviaje_destino)
        val horaInicio: TextView = view.findViewById(R.id.startviaje_horaorigen)
        val horaFin : TextView = view.findViewById(R.id.startviaje_horadestino)
        origen.text = viaje.paradas.get(0).ubicacion
        destino.text = viaje.paradas.get(1).ubicacion
        horaInicio.text = viaje.horaInicio
        horaFin.text = viaje.horaFin

        val viajeService = ApiClient.retrofit.create(ViajeApiService::class.java)

        viajeService.getPasajerosPorViaje(viaje.id).enqueue(object: Callback<List<PasajeroEnLista>> {
            override fun onFailure(call: Call<List<PasajeroEnLista>>, t: Throwable) {
                Log.i("Fallamos", "No se pudieron cargas los pasajeros en el Fragment Passenger List")
            }

            override fun onResponse(
                call: Call<List<PasajeroEnLista>>,
                response: Response<List<PasajeroEnLista>>
            ) {
                if(response.isSuccessful){
                    adapterPasajerosEnViaje = PasajerosEnViajeRecyclerAdapter(requireContext())
                    recyclerPasajerosEnViaje.adapter = adapterPasajerosEnViaje
                    adapterPasajerosEnViaje.submitList(response.body()!! as ArrayList)
                }
            }
        })

        buttonFinalizar.setOnClickListener{
            cargarDialogoFinalizarViaje(viaje.id)
        }
        return view
    }

    fun cargarDialogoFinalizarViaje(viajeId: Long){
        val dialog: AlertDialog


        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_finish_viaje, null)

        val confirmButton = view.findViewById<Button>(R.id.boton_aceptar_finish_viaje)
        val cancelButton = view.findViewById<Button>(R.id.boton_cancelar_finish_viaje)

        builder.setView(view)

        dialog = builder.create()
        confirmButton.setOnClickListener{
            finalizarViaje(viajeId, dialog)
            actualizarPasajerosEnDestino(viajeId)
        }

        cancelButton.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()


    }


    fun finalizarViaje(viajeId: Long, dialog: AlertDialog){

        viajeService.actualizarEstadoViaje(viajeId, "FINALIZADO").enqueue(object: Callback<Int>{
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if(response.isSuccessful){
                    if(response.body()!! == 1){

                        dialog.dismiss()

                    }
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo al finalizar el viaje", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun actualizarPasajerosEnDestino(viajeId: Long){
        solicitudService.pasajerosEnDestino(viajeId).enqueue(object: Callback<Int>{
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    if(response.body()!! >= 1){
                        Toast.makeText(requireContext(), "El viaje ha finalizado. Todos han llegado a su destino", Toast.LENGTH_SHORT).show()
                        Log.i("En destino", "Pasajeros en destino")
                        requireActivity().finish()
                    }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.i("Fallo", "Pasajeros no estan destino")
            }
        })
    }



}