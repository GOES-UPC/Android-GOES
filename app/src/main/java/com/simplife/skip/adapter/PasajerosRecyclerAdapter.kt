package com.simplife.skip.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.simplife.skip.R
import com.simplife.skip.models.PasajeroEnLista
import com.simplife.skip.models.Usuario
import kotlinx.android.synthetic.main.pasajero_list_item.view.*

class PasajerosRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items : List<PasajeroEnLista> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MiPasajeroViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.pasajero_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is MiPasajeroViewHolder->{
                holder.bind(items.get(position))
            }
        }
    }

    fun submitList(pasajeroList: List<PasajeroEnLista>){
        items = pasajeroList
    }

    class MiPasajeroViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){

        val pasajeroName = itemView.findViewById<TextView>(R.id.pasajero_name)
        val pasajeroImage = itemView.findViewById<ImageView>(R.id.pasajero_image)
        val pasajeroParada = itemView.findViewById<TextView>(R.id.pasajero_punto_encuentro)

        fun bind(pasajero: PasajeroEnLista){
            pasajeroName.setText(pasajero.nombres)
            pasajeroParada.setText(pasajero.puntoEncuentro)

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(pasajero.imagen)
                .into(pasajeroImage)

        }

    }


}