package com.simplife.skip.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.simplife.skip.R
import com.simplife.skip.interfaces.SolicitudApiService
import com.simplife.skip.models.PasajeroEnLista
import com.simplife.skip.util.ApiClient
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PasajerosEnViajeRecyclerAdapter(var context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    var items: ArrayList<PasajeroEnLista> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PasajeroEnViajeViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.passenger_on_trip_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(items: ArrayList<PasajeroEnLista>){
        this.items = items
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is PasajeroEnViajeViewHolder -> {
                holder.bind(items.get(position))
                val pos = holder.adapterPosition
                val pasajero = items.get(pos)
                val checkBox = holder.checkBox
                if(pasajero.estadoPasajero == "En viaje"){
                    checkBox.isChecked = true
                    checkBox.isEnabled = false
                } else {
                    checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) {
                            Log.i("Checked!", "Pasajero ID ${pasajero.usuarioId} está en el viaje")
                            actualizarEstadoPasajero("En viaje", pasajero.viajeId, pasajero.usuarioId, pasajero.nombres)
                            checkBox.isEnabled = false
                            pasajero.estadoPasajero = "En viaje"
                            notifyItemChanged(pos)
                        }

                    }
                }

            }
        }
    }

    inner class PasajeroEnViajeViewHolder constructor(view: View): RecyclerView.ViewHolder(view){
        val nombrePasajero = view.findViewById<TextView>(R.id.pasajero_name)
        val puntoEncuentro = view.findViewById<TextView>(R.id.pasajero_punto_encuentro)
        val estado = view.findViewById<TextView>(R.id.pasajero_estado)
        val avatar = view.findViewById<CircleImageView>(R.id.avatar_pasajero)
        val checkBox: CheckBox = view.findViewById(R.id.change_state_to_on_trip)




        fun bind(p: PasajeroEnLista){
            nombrePasajero.text = p.nombres
            puntoEncuentro.text = p.puntoEncuentro
            estado.text = p.estadoPasajero

            Glide.with(context)
                .load(p.imagen)
                .into(avatar)




        }
    }

    fun actualizarEstadoPasajero(estado: String, viajeId: Long, pasajeroId: Long, nombres: String){
        val solicitudService = ApiClient.retrofit.create(SolicitudApiService::class.java)

        solicitudService.actualizarEstadoPasajero(viajeId, pasajeroId, estado).enqueue(object :
            Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    if (response.body()!! == 1) {
                        Toast.makeText(context, "${nombres} está en el viaje", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Toast.makeText(
                    context,
                    "No se puedo actualizar el estado del pasajero",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }
}