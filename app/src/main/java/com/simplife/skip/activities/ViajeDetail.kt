package com.simplife.skip.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.media.Image
import com.simplife.skip.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.simplife.skip.adapter.ResenasRecyclerAdapter
import com.simplife.skip.adapter.ViajeRecyclerAdapter
import com.simplife.skip.interfaces.*
import com.simplife.skip.models.*
import com.simplife.skip.util.API_KEY
import com.simplife.skip.util.Resenas_Data
import com.simplife.skip.util.URL_API
import com.simplife.skip.util.URL_API_GOOGLE_MAPS
import kotlinx.android.synthetic.main.activity_viaje_detail.*
import kotlinx.android.synthetic.main.dialog_solicitar.*
import kotlinx.android.synthetic.main.viaje_list_item.view.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ViajeDetail : AppCompatActivity() {

    private lateinit var back_btn: ImageButton

    private  lateinit var viajeAdapter: ResenasRecyclerAdapter
    private lateinit var recyclerView: RecyclerView

    private lateinit var viajeService: ViajeApiService
    private lateinit var reportService: ReportApiService
    private lateinit var solicitudService: SolicitudApiService
    private lateinit var staticmapService :StaticMapApiService

    private lateinit var btn_solicitar : Button
    private lateinit var iv_staticMap : ImageView

      var viajeid = 0L


    private lateinit var prefs : SharedPreferences
    private lateinit var edit: SharedPreferences.Editor

    private lateinit var mapFragment :SupportMapFragment
    private lateinit var et_dialog_solicitar_message : EditText
    private lateinit var dialog :AlertDialog


    private lateinit var requestOptions: RequestOptions

    @SuppressLint("MissingPermission")
    private val mapCallback = OnMapReadyCallback{ googleMap ->
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        val Lima = LatLng(-12.0554671, -77.0431111)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Lima, 11.0f))

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viaje_detail)

        viajeid = intent.getSerializableExtra("via") as Long
        back_btn = findViewById(R.id.detback_button)

        prefs = getSharedPreferences("user", Context.MODE_PRIVATE)
        edit= prefs.edit()

        btn_solicitar = findViewById(R.id.btn_solicitar)
        iv_staticMap = findViewById(R.id.iv_staticmap)


        requestOptions = RequestOptions()
            .placeholder(android.R.color.white)
            .error(android.R.color.white)

        Glide.with(applicationContext)
            .applyDefaultRequestOptions(requestOptions)
            .load(android.R.color.white)
            .into(iv_staticMap)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        val retrofitStaticMap = Retrofit.Builder()
            .baseUrl(URL_API_GOOGLE_MAPS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        viajeService = retrofit.create(ViajeApiService::class.java)
        solicitudService = retrofit.create(SolicitudApiService::class.java)
        reportService = retrofit.create(ReportApiService::class.java)
        staticmapService = retrofitStaticMap.create(StaticMapApiService::class.java)




        viajeService.getHomeViajesPorId(viajeid).enqueue(object : Callback<ViajeInicio> {
            override fun onResponse(call: Call<ViajeInicio>?, response: Response<ViajeInicio>?) {
                val viaje = response?.body()

                viajedetail_author.setText(viaje?.nombres)
                viajedetail_title.setText(viaje?.fechaPublicacion)
                viajedetail_text.setText(viaje?.descripcion)
                viajedetail_origen.setText(viaje?.paradas!![0].ubicacion)
                viajedetail_destino.setText(viaje?.paradas!![1].ubicacion)
                viajedetail_hora_destino.setText(viaje?.horaFin)
                viajedetail_hora_origen.setText(viaje?.horaInicio)

                Glide.with(applicationContext)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(viaje?.imagen)
                    .into(viajedetail_image)

                cargarStaticMapRoute(viaje?.paradas[0],viaje?.paradas[1])


            }
            override fun onFailure(call: Call<ViajeInicio>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })



        //Lista de Resenas
        recyclerView = findViewById(R.id.recycler_resenas_view)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        //val topSpacingDecoration = TopSpacingItemDecoration(30)
        //recyclerView.addItemDecoration(topSpacingDecoration)
        resenasData()
        //Fin de lista de Resenas


        back_btn.setOnClickListener{
            finish()
        }
        btn_solicitar.setOnClickListener {
            dialogSolicitar()
        }


        /*mapFragment = supportFragmentManager.findFragmentById(R.id.mapViajeDetail) as SupportMapFragment
        mapFragment?.getMapAsync(mapCallback)*/

    }

    fun resenasData() {
        reportService!!.getReportesporViaje(viajeid).enqueue(object: Callback<List<Reporte>> {
            override fun onResponse(call: Call<List<Reporte>>, response: Response<List<Reporte>>) {
                val viajesaux = response.body()

                var sorted = viajesaux!!.sortedWith(compareByDescending ({it.id}))

                viajeAdapter = ResenasRecyclerAdapter()
                recyclerView.adapter = viajeAdapter
                viajeAdapter.submitList(viajesaux)
            }
            override fun onFailure(call: Call<List<Reporte>>?, t: Throwable?) {
                t?.printStackTrace()
                Toast.makeText(applicationContext,"Ha ocurrido un error",Toast.LENGTH_SHORT).show()
            }
        })
    }





    fun cargarStaticMapRoute(origen:Parada, destino:Parada)
    {
        var origin = "${origen.latitud},${origen.longitud}"
        var destination = "${destino.latitud},${destino.longitud}"
        var size= "800x800"
        var key = API_KEY

        staticmapService.getRoutes(origin,destination,"driving", key).enqueue(object :Callback<GoogleMapDirections>{
            override fun onResponse(call: Call<GoogleMapDirections>, response: Response<GoogleMapDirections>) {
                val googleMapDirection = response.body()
                val points = googleMapDirection!!.routes[0].overview_polyline.points
                Log.i("Entro", points)

                Glide.with(applicationContext)
                    .load("https://maps.googleapis.com/maps/api/staticmap?size=$size&path=enc%3A$points&key=$key")
                    .into(iv_staticMap)
            }
            override fun onFailure(call: Call<GoogleMapDirections>, t: Throwable) {
                Log.e("F", "LA ptmr")
                Log.e("F", t.message.toString())
            }
        })
    }

    fun solicitarViaje()
    {
        val parada = Parada(latitud = 12.131231,longitud = 13.123213,ubicacion = "En algun lugar de un gran pais")
        var solicitd = SolicitudRequest(mensaje = et_dialog_solicitar_message.text.toString(),pasajeroId = prefs.getLong("idusuario",0L), viajeId = viajeid, puntoEncuentro = parada)

        solicitudService.solicitarViaje(solicitd).enqueue(object : Callback<Solicitud> {
            override fun onResponse(call: Call<Solicitud>?, response: Response<Solicitud>?) {
                val soli = response!!.body()
                if(soli == null){
                    Toast.makeText(this@ViajeDetail, "No se puede solicitar un viaje propio", Toast.LENGTH_SHORT).show()
                }
                Log.i("Solicitud",soli.toString())
                dialog.dismiss()
                finish()
            }
            override fun onFailure(call: Call<Solicitud>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }

    fun dialogSolicitar()
    {
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        //https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=12&size=400x400&key=YOUR_API_KEY
        val builer = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val view  = inflater.inflate(R.layout.dialog_solicitar,null)
        builer.setView(view)

        dialog =  builer.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val staticMap = view.findViewById(R.id.iv_dialog_map) as ImageView
        val solicitarButton = view.findViewById(R.id.btn_dialog_solicitar) as Button
        et_dialog_solicitar_message = view.findViewById(R.id.et_solicitar_mensaje)
        var center = ""
        var zoom = ""
        var size = ""

        val url = "$URL_API_GOOGLE_MAPS/staticmap?"


        Glide.with(applicationContext)
            .load("https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=11&size=800x800&maptype=roadmap&key=AIzaSyBBqph0jQU_8qqrqypG35bQazc29sUanjo")
            .thumbnail(Glide.with(this).load(R.drawable.loading))
            .into(staticMap)

        solicitarButton.setOnClickListener {
            if (et_dialog_solicitar_message.text.isNullOrEmpty())
            {
                Toast.makeText(this,"Complete los campos",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            solicitarViaje()
        }
    }

}
