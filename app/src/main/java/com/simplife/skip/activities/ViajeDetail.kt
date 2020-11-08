package com.simplife.skip.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.InflateException
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

import androidx.core.app.ShareCompat

import androidx.constraintlayout.widget.ConstraintLayout

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.simplife.skip.R
import com.simplife.skip.adapter.ResenasRecyclerAdapter
import com.simplife.skip.interfaces.*
import com.simplife.skip.models.*
import com.simplife.skip.util.API_KEY
import com.simplife.skip.util.URL_API
import com.simplife.skip.util.URL_API_GOOGLE_MAPS
import kotlinx.android.synthetic.main.activity_viaje_detail.*
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
    private lateinit var btn_cancelar: Button
    private lateinit var iv_staticMap : ImageView

    var viajeid = 0L

    private lateinit var viajePublicado :ViajeInicio

    private lateinit var prefs : SharedPreferences
    private lateinit var edit: SharedPreferences.Editor

    private lateinit var mapFragment :SupportMapFragment
    private lateinit var et_dialog_solicitar_message : EditText
    private lateinit var dialog :AlertDialog


    private lateinit var requestOptions: RequestOptions

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private lateinit var mylocation: LatLng

    private  var marker : Marker? = null

    private var viewDialog : View? = null

    private lateinit var viewlayout:ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viaje_detail)

        val uri = intent.data
        if(uri != null){
            //www.goesapp.com/viajes?id=...
            val id = uri.getQueryParameter("id")!!.toLong()
            viajeid = id;
        } else {
            viajeid = intent.getSerializableExtra("via") as Long
        }

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
            .asGif()
            .load(R.drawable.loading)
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
                viajePublicado = viaje!!
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


        viewlayout = findViewById( R.id.mainmenu) as ConstraintLayout
        viewlayout.foreground.alpha =  0;

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
        var size= "1000x1000"
        var key = API_KEY

        Log.i("Direcctions", "https://maps.googleapis.com/maps/api/directions/json?origin=$origin&destination=$destination&mode=driving&key=$API_KEY")
        staticmapService.getRoutes(origin,destination,"driving", key).enqueue(object :Callback<GoogleMapDirections>{
            override fun onResponse(call: Call<GoogleMapDirections>, response: Response<GoogleMapDirections>) {
                val googleMapDirection = response.body()
                val points = googleMapDirection!!.routes[0].overview_polyline.points
                Log.i("Entro", "https://maps.googleapis.com/maps/api/staticmap?size=$size&path=enc%3A$points&key=$key")

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

    fun solicitarViaje(mensaje: String)
    {
        val parada = Parada(latitud = marker!!.position.latitude,longitud = marker!!.position.longitude, ubicacion = mensaje)
        var solicitd = SolicitudRequest(mensaje = et_dialog_solicitar_message.text.toString(),pasajeroId = prefs.getLong("idusuario",0L), viajeId = viajeid, puntoEncuentro = parada)

        solicitudService.solicitarViaje(solicitd).enqueue(object : Callback<Solicitud> {
            override fun onResponse(call: Call<Solicitud>?, response: Response<Solicitud>?) {
                val soli = response!!.body()
                if(soli!!.id == 0L){
                    Toast.makeText(this@ViajeDetail, "No se puede solicitar un viaje propio", Toast.LENGTH_SHORT).show()
                    return
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

    @SuppressLint("MissingPermission")
    private val mapCallback = OnMapReadyCallback{ googleMap ->
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        val Lima = LatLng(-12.0554671, -77.0431111)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Lima, 11.0f))

        googleMap.setOnMapClickListener {
            if(marker == null)
            {
                marker = googleMap.addMarker(MarkerOptions().position(it).draggable(true))
            }
            else
            {
                marker!!.remove()
                marker = null
                marker = googleMap.addMarker(MarkerOptions().position(it).draggable(true))
            }
        }
        val latOrigen = LatLng(viajePublicado.paradas[0].latitud,viajePublicado.paradas[0].longitud)
        val latDestino = LatLng(viajePublicado.paradas[1].latitud,viajePublicado.paradas[1].longitud)
        googleMap.addMarker(MarkerOptions().position(latOrigen).title("Origen").draggable(false))
        googleMap.addMarker(MarkerOptions().position(latDestino).title("Destino").draggable(false))
        dibujarRuta(latOrigen,latDestino,googleMap)
        val builder = LatLngBounds.Builder()
        builder.include(latOrigen).include(latDestino)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),0))
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(googleMap.cameraPosition.zoom -1))
    }

    fun dialogSolicitar()
    {

        viewlayout.foreground.alpha = 220
        //https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=12&size=400x400&key=YOUR_API_KEY
        val builer = AlertDialog.Builder(this, R.style.DialogTheme)
        if (viewDialog != null)
        {
            val parent = viewDialog!!.parent as ViewGroup
            if (parent != null)
            {
                parent.removeView(viewDialog)
            }
        }
        try {
            viewDialog  = layoutInflater.inflate(R.layout.dialog_solicitar,null)

        }catch (e:InflateException) {

        }

        builer.setView(viewDialog)
        dialog =  builer.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        Log.i("AH",dialog.window.toString());
        dialog.setOnDismissListener {
            viewlayout.foreground.alpha = 0
        }
        btn_cancelar = viewDialog!!.findViewById(R.id.btn_dialog_cancelar) as Button
        btn_cancelar.setOnClickListener{
            dialog.dismiss()
        }

        val fragmentMap = supportFragmentManager.findFragmentById(R.id.fr_dialog_map) as SupportMapFragment
        val solicitarButton = viewDialog!!.findViewById(R.id.btn_dialog_solicitar) as Button
        et_dialog_solicitar_message = viewDialog!!.findViewById(R.id.et_dialog_solicitar_message)
        fragmentMap.getMapAsync(mapCallback)

        solicitarButton.setOnClickListener {
            if (et_dialog_solicitar_message.text.isNullOrEmpty())
            {
                Toast.makeText(this,"Complete los campos",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (marker == null)
            {
                Toast.makeText(this,"Elija una parada",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            solicitarViaje(et_dialog_solicitar_message.text.toString())
        }
    }

    private fun dibujarRuta(origen:LatLng,destino:LatLng,googleMap: GoogleMap)
    {
        val origen_lat = origen.latitude
        val origen_lng = origen.longitude
        val destino_lat = destino.latitude
        val destino_lng = destino.longitude

        staticmapService.getRoutes("$origen_lat,$origen_lng","$destino_lat,$destino_lng","driving", API_KEY).enqueue(object :Callback<GoogleMapDirections>{
            override fun onResponse(call: Call<GoogleMapDirections>, response: Response<GoogleMapDirections>) {
                val googleMapDirection = response.body()
                val points = googleMapDirection!!.routes[0].overview_polyline.points
                Log.i("Entro", points)
                googleMap.addPolyline(PolylineOptions().addAll(PolyUtil.decode(points)).width(7f).color(Color.RED))
            }
            override fun onFailure(call: Call<GoogleMapDirections>, t: Throwable) {
                Log.e("F", t.message.toString())

            }
        })
    }


    @SuppressLint("MissingPermission")
    private fun traerUbicacion() {

        locationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(20 * 1000)
        locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    return;
                }
                for (location in locationResult.getLocations()) {
                    if (location != null) {
                        mylocation = LatLng(location.latitude, location.longitude)
                    }
                }
                super.onLocationResult(locationResult)
            }
        }
        mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }



}
