package com.simplife.skip.interfaces


import android.media.Image
import android.util.Size
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StaticMapApiService {

    //https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=11&size=400x400&maptype=roadmap&key=AIzaSyBBqph0jQU_8qqrqypG35bQazc29sUanjo
    @GET("maps/api/staticmap")
    fun getStaticMap(@Query("center")center:String,@Query("zoom") zoom : String,
                     @Query("size") size: String, @Query("key") key:String  ): Call<ResponseBody>
    @GET("maps/api/staticmap")
    fun getStaticMapRoute(@Query("size") size: String, @Query("path") path : String, @Query("key") key:String  ): Call<ResponseBody>

    //https://maps.googleapis.com/maps/api/directions/json?origin=Grodno&destination=Minsk&mode=driving&key=AIzaSyBBqph0jQU_8qqrqypG35bQazc29sUanjo
    @GET("maps/api/directions/json")
    fun getRoutes(@Query("origin") origin: String, @Query("destination") destination : String,
                   @Query("mode") mode: String ,@Query("key") key:String  ): Call<GoogleMapDirections>

}