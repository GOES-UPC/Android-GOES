package com.simplife.skip.interfaces

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONArray
import java.io.Serializable

class GoogleMapDirections(

    var geocoded_waypoints : Any,
    var routes: List<Routes>,
    var status : String
): Serializable {
    override fun toString(): String {
        return "GoogleMapDirections(geocoded_waypoints=$geocoded_waypoints, routes=$routes, status='$status')"
    }
}


class GeoCodeWayPoints(

    var geocoder_status: String,
    var place_id: String,
    var types : List<String>

):Serializable{
    override fun toString(): String {
        return "GeoCodeWayPoints(geocoder_status='$geocoder_status', place_id='$place_id', types=$types)"
    }

}

class Routes(
    var boundes: Any,
    var copyrights : String,
    var legs: Any,
    var overview_polyline: OverView,
    var summary: String,
    var warnings: Any,
    var waypoints_order: Any
):Serializable {
    override fun toString(): String {
        return "Routes(boundes=$boundes, copyrights='$copyrights', legs=$legs, overview_polyline=$overview_polyline, summary='$summary', warnings=$warnings, waypoints_order=$waypoints_order)"
    }
}

class OverView(
    var points : String
):Serializable {
    override fun toString(): String {
        return "OverView(points='$points')"
    }
}