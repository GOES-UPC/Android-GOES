package com.simplife.skip.adapter

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.simplife.skip.R
import com.simplife.skip.models.PasajeroEnLista
import com.simplife.skip.models.PasajeroEnViaje
import de.hdodenhof.circleimageview.CircleImageView

class PasajerosEnViajeRecyclerAdapter(var context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    var items: ArrayList<PasajeroEnLista> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PasajeroEnViajeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.passenger_on_trip_item, parent, false)
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
            }
        }
    }

    inner class PasajeroEnViajeViewHolder constructor(view: View): RecyclerView.ViewHolder(view){
        val nombrePasajero = view.findViewById<TextView>(R.id.pasajero_name)
        val puntoEncuentro = view.findViewById<TextView>(R.id.pasajero_punto_encuentro)
        val estado = view.findViewById<TextView>(R.id.pasajero_estado)
        val avatar = view.findViewById<CircleImageView>(R.id.avatar_pasajero)

        fun bind(p: PasajeroEnLista){
            nombrePasajero.text = p.nombres
            puntoEncuentro.text = p.puntoEncuentro
            estado.text = p.estadoPasajero

            Glide.with(context)
                .load(p.imagen)
                .into(avatar)
        }
    }
}