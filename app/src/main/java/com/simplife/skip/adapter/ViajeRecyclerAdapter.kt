package com.simplife.skip.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.simplife.skip.R
import com.simplife.skip.activities.ViajeDetail
import com.simplife.skip.interfaces.GoogleMapDirections
import com.simplife.skip.interfaces.StaticMapApiService
import com.simplife.skip.models.Parada
import com.simplife.skip.models.ViajeInicio
import com.simplife.skip.util.API_KEY
import com.simplife.skip.util.URL_API_GOOGLE_MAPS
import kotlinx.android.synthetic.main.activity_viaje_detail.*
import kotlinx.android.synthetic.main.viaje_list_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

class ViajeRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<ViajeInicio> = ArrayList()
    private lateinit var context: Fragment


    private var viewDialog: View? = null
    private lateinit var dialog : android.app.AlertDialog

    private lateinit var staticmapService : StaticMapApiService


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val retrofitStaticMap = Retrofit.Builder()
            .baseUrl(URL_API_GOOGLE_MAPS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        staticmapService = retrofitStaticMap.create(StaticMapApiService::class.java)

        return ViajeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.viaje_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ViajeViewHolder -> {
                holder.bind(items.get(position))
            }
        }
    }


    fun submitList(viajeList: List<ViajeInicio>){
        items = viajeList
    }

    fun submitContext(context: Fragment){
        this.context = context
    }

    fun getContext(): Fragment{
        return this.context
    }

    inner class ViajeViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){

        val userImage = itemView.viaje_image
        val viajeTitle = itemView.viaje_title
        val author = itemView.viaje_author
        val viajeText = itemView.viaje_text
        val viajeSource = itemView.viaje_origen
        val viajeDestiny = itemView.viaje_destino
        val viajeHoraOrigen = itemView.viaje_hora_origen
        val viajeHoraDestino = itemView.viaje_hora_destino

        fun bind(viaje: ViajeInicio){
            viajeTitle.setText(viaje.fechaPublicacion)
            author.setText(viaje.nombres)
            viajeText.setText(viaje.descripcion)
            viajeSource.setText(viaje.paradas[0].ubicacion)
            viajeDestiny.setText(viaje.paradas[1].ubicacion)
            viajeHoraDestino.setText(viaje.horaFin)
            viajeHoraOrigen.setText(viaje.horaInicio)

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(viaje.imagen)
                .into(userImage)

            itemView.setOnClickListener{
                //mostrarDialogViaje(viaje.paradas[0], viaje.paradas[1]);
                getContext().startActivityForResult(Intent(itemView.context, ViajeDetail::class.java).putExtra("via", viaje.id),333)
            }
        }
    }


    //----------------------------------TO DO ---------------------------------------------//
    @SuppressLint("UseRequireInsteadOfGet")
    fun mostrarDialogViaje(origen: Parada, destino: Parada)
    {
        //https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=12&size=400x400&key=YOUR_API_KEY
        val builer = android.app.AlertDialog.Builder(getContext().context!!)
        if (viewDialog != null)
        {
            val parent = viewDialog!!.parent as ViewGroup
            if (parent != null)
            {
                parent.removeView(viewDialog)
            }
        }
        try {
            viewDialog  = getContext().activity!!.layoutInflater.inflate(
                R.layout.dialog_solicitar2,
                null
            )
        }catch (e: InflateException) {

        }


        builer.setView(viewDialog)
        dialog =  builer.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val solicitarButton = viewDialog!!.findViewById(R.id.btn_dialog_solicitar) as Button
        val iv_staticMap = viewDialog!!.findViewById(R.id.iv_staticmap) as ImageView


        Glide.with(context!!)
            .applyDefaultRequestOptions(RequestOptions()
                    .placeholder(android.R.color.white)
                    .error(android.R.color.white))
            .asGif()
            .load(R.drawable.loading)
            .into(iv_staticMap)
        /*Glide.with(builer.context)
            .load("https://i.ibb.co/xms0K5f/black-clover-natch-by-srlmao-de83w12-fullview.jpg")
            .into(iv_staticMap)*/

        cargarStaticMapRoute(origen, destino, iv_staticMap)
        solicitarButton.setOnClickListener {
            Toast.makeText(getContext().context, origen.toString() + " "+destino.toString(), Toast.LENGTH_SHORT).show()
        }



    }

    fun cargarStaticMapRoute(origen: Parada, destino: Parada, iv_staticMap: ImageView)
    {
        var origin = "${origen.latitud},${origen.longitud}"
        var destination = "${destino.latitud},${destino.longitud}"
        var size= "1000x1000"
        var key = API_KEY

        staticmapService.getRoutes(origin,destination,"driving", key).enqueue(object :
            Callback<GoogleMapDirections> {
            override fun onResponse(call: Call<GoogleMapDirections>, response: Response<GoogleMapDirections>) {
                val googleMapDirection = response.body()
                val points = googleMapDirection!!.routes[0].overview_polyline.points

                Glide.with(context!!)
                    .load("https://maps.googleapis.com/maps/api/staticmap?size=$size&path=enc%3A$points&key=$key")
                    .into(iv_staticMap)
            }
            override fun onFailure(call: Call<GoogleMapDirections>, t: Throwable) {
                Log.e("F", t.message.toString())
            }
        })
    }
}
