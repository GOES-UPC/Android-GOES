package com.simplife.skip.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
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
import com.simplife.skip.interfaces.GoogleMapDirections
import com.simplife.skip.interfaces.StaticMapApiService
import com.simplife.skip.interfaces.UsuarioApiService
import com.simplife.skip.interfaces.ViajeApiService
import com.simplife.skip.models.Parada
import com.simplife.skip.models.Usuario
import com.simplife.skip.models.Viaje
import com.simplife.skip.models.ViajeRequest
import com.simplife.skip.util.API_KEY
import com.simplife.skip.util.URL_API
import com.simplife.skip.util.URL_API_GOOGLE_MAPS
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_viaje_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random.Default.nextInt

class Post : AppCompatActivity() {

    //Layout
    private lateinit var backbtn: ImageButton
    private lateinit var postBtn: Button
    private lateinit var description: EditText
    private lateinit var origen: EditText
    private lateinit var destino: TextView
    private lateinit var origen_hora: EditText
    private lateinit var destino_hora: EditText
    private lateinit var fecha_viaje: EditText
    private lateinit var img_btn_ida : ImageButton


    private lateinit var origenSpinner: Spinner

    private lateinit var usuarioService: UsuarioApiService
    private lateinit var viajeService: ViajeApiService
    private lateinit var googleMapService : StaticMapApiService


    var usuario: Usuario? = null

    private lateinit var mylocation: LatLng

    //Api de Google Maps
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var senseText: TextView

    private var markerOrigen: Marker? = null
    private var markerDestino: Marker? = null

    private var polyline :Polyline? = null


    private lateinit var mapFragment: SupportMapFragment

    //SharedPreferences
    private lateinit var prefs: SharedPreferences
    private lateinit var edit: SharedPreferences.Editor

    private var ida  = true

    @SuppressLint("MissingPermission")
    private val mapCallback = OnMapReadyCallback { googleMap ->
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        val Lima = LatLng(-12.0554671, -77.0431111)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Lima, 13.0f))



        googleMap.setOnMapClickListener {
            if (markerOrigen != null && markerDestino != null) {
                return@setOnMapClickListener
            }
            val markerOptions =
                MarkerOptions().position(it).anchor(0.5f, 0.5f).flat(false).draggable(true)

            if (markerOrigen == null) {
                markerOptions.title("Origen")
                markerOrigen = googleMap.addMarker(markerOptions)
                //origen.setText(markerOrigen!!.position.toString())
            } else {
                markerOptions.title("Destino")
                markerDestino = googleMap.addMarker(markerOptions)
                if (!geocoder.getFromLocation(it.latitude, it.longitude, 1).isEmpty()) {
                    val addres = geocoder.getFromLocation(it.latitude, it.longitude, 1).get(0)!!
                        .getAddressLine(0)!!.toString()
                    destino.setText(addres)
                    dibujarRuta(markerOrigen!!.position,markerDestino!!.position,googleMap)
                }
            }
        }

        googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragEnd(p0: Marker?) {
                if (p0 == markerDestino) {
                    if (!geocoder.getFromLocation(
                            p0!!.position.latitude,
                            p0!!.position.longitude,
                            1
                        ).isEmpty()
                    ) {
                        val addres = geocoder.getFromLocation(
                            p0!!.position.latitude,
                            p0!!.position.longitude,
                            1
                        ).get(0)!!.getAddressLine(0)!!.toString()
                        destino.setText(addres)
                        dibujarRuta(markerOrigen!!.position,markerDestino!!.position,googleMap)
                    }
                }
            }

            override fun onMarkerDragStart(p0: Marker?) {
            }

            override fun onMarkerDrag(p0: Marker?) {
            }
        })
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        prefs = getSharedPreferences("user", Context.MODE_PRIVATE)
        edit = prefs.edit()

        val usuarioid = prefs.getLong("idusuario", 0)

        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofitMaps: Retrofit = Retrofit.Builder()
            .baseUrl(URL_API_GOOGLE_MAPS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        usuarioService = retrofit.create(UsuarioApiService::class.java)
        viajeService = retrofit.create(ViajeApiService::class.java)
        googleMapService = retrofitMaps.create(StaticMapApiService::class.java)

        mFusedLocationClient = FusedLocationProviderClient(this)

        val array =
            arrayListOf("Sede:", "Moterrico", "San Isidro", "San Miguel", "Villa")
        val arrayLatLng = arrayListOf(
            null,
            LatLng(-12.1040839, -76.9630173),
            LatLng(-12.0874715,-77.0500334),
            LatLng(-12.076169,-77.0935428),
            LatLng(-12.1978174, -77.0086914)
        )

        postBtn = findViewById(R.id.post_btn)
        backbtn = findViewById(R.id.postback_button)
        description = findViewById(R.id.post_description)
        //origen = findViewById(R.id.post_origen)
        origenSpinner = findViewById(R.id.origen_spinner)
        destino = findViewById(R.id.post_destino)
        origen_hora = findViewById(R.id.post_hora_origen)
        destino_hora = findViewById(R.id.post_hora_destino)
        fecha_viaje = findViewById(R.id.fechaProgrmada)
        img_btn_ida = findViewById(R.id.img_btn_ida)
        senseText = findViewById(R.id.sense_txt)
        post_author.setText(prefs.getString("nombres","Nombre")+" "+prefs.getString("apellidos",""))

        Glide.with(applicationContext)
            .applyDefaultRequestOptions(requestOptions)
            .load(prefs.getString("imagen",""))
            .into(post_image)

        geocoder = Geocoder(this, Locale.getDefault())

        val adapter = ArrayAdapter<String>(this, R.layout.custom_spinner_layout, array)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        origenSpinner.adapter = adapter
        origenSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    if (markerOrigen != null) {
                        markerOrigen!!.remove(); markerOrigen = null
                    }; return
                } else {

                    mapFragment.getMapAsync { googleMap ->
                        if (markerOrigen == null) {
                            markerOrigen = googleMap.addMarker(
                                MarkerOptions().position(arrayLatLng[position]!!).title("Origen")
                                    .flat(false).draggable(false)
                            )
                        } else {
                            markerOrigen!!.position = arrayLatLng[position]
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        traerUbicacion()
        mapFragment = supportFragmentManager.findFragmentById(R.id.mapPostViaje) as SupportMapFragment
        mapFragment?.getMapAsync(mapCallback)

        fecha_viaje.setOnClickListener {
            datePicker()
        }

        origen_hora.setOnClickListener {
            timePickerOrigen()
        }
        destino_hora.setOnClickListener {
            timePickerDestino()
        }

        backbtn.setOnClickListener {
            finish()
        }

        postBtn.setOnClickListener {
            if(origen_hora.text.isNullOrEmpty() || destino_hora.text.isNullOrEmpty() || description.text.isNullOrEmpty() || fecha_viaje.text.isNullOrEmpty())
            {
                Toast.makeText(this, "Complete todos los campos",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            publicarViaje(usuarioid)
        }
        img_btn_ida.setOnClickListener{
            if (ida)
            {
                senseText.text = "Vuelta"
                animateImage()
            }
            else{
                senseText.text = "  Ida  "
                animateImageback()
            }
            ida = !ida
        }
    }

    private fun animateImage(){
        val rotate = AnimationUtils.loadAnimation(this,R.anim.rotate_animation)
        img_btn_ida.animation = rotate
    }

    private fun animateImageback(){
        val rotate2 = AnimationUtils.loadAnimation(this,R.anim.rotate_animation_back)
        img_btn_ida.animation = rotate2
    }

    private fun dibujarRuta(origen:LatLng,destino:LatLng,googleMap: GoogleMap)
    {
        val origen_lat = origen.latitude
        val origen_lng = origen.longitude
        val destino_lat = destino.latitude
        val destino_lng = destino.longitude

        googleMapService.getRoutes("$origen_lat,$origen_lng","$destino_lat,$destino_lng","driving", API_KEY).enqueue(object :Callback<GoogleMapDirections>{
            override fun onResponse(call: Call<GoogleMapDirections>, response: Response<GoogleMapDirections>) {
                val googleMapDirection = response.body()
                val points = googleMapDirection!!.routes[0].overview_polyline.points
                Log.i("Entro", points)
                if(polyline == null)
                {
                    polyline = googleMap.addPolyline(PolylineOptions().addAll(PolyUtil.decode(points)).width(7f).color(Color.RED))
                }
                else
                {
                    polyline!!.remove()
                    polyline = null
                    polyline = googleMap.addPolyline(PolylineOptions().addAll(PolyUtil.decode(points)).width(5f).color(Color.RED))
                }
            }
            override fun onFailure(call: Call<GoogleMapDirections>, t: Throwable) {
                Log.e("F", "LA ptmr")
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


    fun publicarViaje(usuarioid: Long){

        val distancia: Int = 20000


        var inicio  = Parada("UPC "+ origenSpinner.selectedItem.toString(), markerOrigen!!.position.latitude, markerOrigen!!.position.longitude)
        var fin  = Parada(destino.text.toString(), markerDestino!!.position.latitude,markerDestino!!.position.longitude)

        var viajeRequest : ViajeRequest
        val horaOrigen = "${origen_hora.text}:00"
        val horaDestino = "${destino_hora.text}:00"
        if(ida)
        {
             viajeRequest = ViajeRequest(usuarioid, true, inicio, fin, "2 horas",
                distancia.toFloat() , description.text.toString(), fecha_viaje.text.toString(), horaOrigen, horaDestino)
        }
        else{
            viajeRequest = ViajeRequest(usuarioid, true, fin, inicio, "2 horas",
                distancia.toFloat() , description.text.toString(), fecha_viaje.text.toString(), horaOrigen, horaDestino)
        }

        viajeService.publicarViaje(viajeRequest).enqueue(object : Callback<Viaje> {
            override fun onResponse(call: Call<Viaje>?, response: Response<Viaje>?) {
                val viaje = response?.body()
                Log.i("Viaje: ", viaje.toString())
                finish()
            }
            override fun onFailure(call: Call<Viaje>?, t: Throwable?) {
                t?.printStackTrace()
            } })

    }

    fun datePicker()
    {
        val c = Calendar.getInstance()
        var mYear = c.get(Calendar.YEAR)
        var mMonth = c.get(Calendar.MONTH)
        var mDay = c.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->

            val y = datePicker.year
            val m = datePicker.month
            val d = datePicker.dayOfMonth
            var mes = ""
            var dia = ""

            if(m<10) mes = "0$m"
            else mes = "$m"
            if (d <10) dia = "0$d"
            else dia = "$d"
            fecha_viaje.setText("$y-$mes-$dia")

        },mYear,mMonth,mDay)
        datePicker.show()
    }

    fun timePickerOrigen()
    {

        val c = Calendar.getInstance()
        var mHora = c.get(Calendar.HOUR_OF_DAY)
        var mMin = c.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
            val h = timePicker.hour
            val m = timePicker.minute
            var hora = ""
            var minuto = ""
            if (h <10) hora = "0$h"
            else hora = h.toString()
            if (m <10) minuto = "0$m"
            else minuto = m.toString()

            origen_hora.setText("$hora:$minuto")
        },mHora,mMin,true)

        timePicker.show()
    }

    fun timePickerDestino()
    {

        val c = Calendar.getInstance()
        var mHora = c.get(Calendar.HOUR_OF_DAY)
        var mMin = c.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
            val h = timePicker.hour
            val m = timePicker.minute
            var hora = ""
            var minuto = ""
            if (h <10) hora = "0$h"
            else hora = h.toString()
            if (m <10) minuto = "0$m"
            else minuto = m.toString()

            destino_hora.setText("$hora:$minuto")
        },mHora,mMin,true)

        timePicker.show()
    }
}

