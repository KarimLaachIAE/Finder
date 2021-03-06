package com.example.finder

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.finder.Common.Common
import com.example.finder.Model.MyPlaces
import com.example.finder.Remote.IGoogleAPIService
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var latitude:Double=0.toDouble()
    private var longitude:Double=0.toDouble()
    private lateinit var mLastLocation: Location
    private var mMarker: Marker?=null

    //Location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    companion object { private const val MY_PERMISSION_CODE: Int = 1000 }

    lateinit var mService:IGoogleAPIService

    internal lateinit var currentPlace:MyPlaces

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Init Service
        mService = Common.googleApiService

        //Request runtime permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkLocationPermission())
            {
                buildLocationRequest()
                buildLocationCallBack()

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

            }
        }
        else
        {
            buildLocationRequest()
            buildLocationCallBack()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }


    }

    private fun nearByPlace(typePlace: String) {
        var url = getUrl(latitude, longitude, typePlace)

        mService.getNearbyPlaces(url)
                .enqueue(object:Callback<MyPlaces>{
                    override fun onFailure(call: Call<MyPlaces>, t: Throwable) {
                        Toast.makeText(baseContext, ""+t.message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<MyPlaces>, response: Response<MyPlaces>) {
                        currentPlace = response.body()!!

                        if(response.isSuccessful)
                        {

                            for(i in 0 until response.body()!!.results!!.size)
                            {
                                val markerOptions=MarkerOptions()
                                val googlePlace = response.body()!!.results!![i]
                                val lat = googlePlace.geometry!!.location!!.lat
                                val lng = googlePlace.geometry!!.location!!.lng
                                val placeName = googlePlace.name
                                val latLng = LatLng(lat, lng)

                                markerOptions.position(latLng)
                                markerOptions.title(placeName)
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                markerOptions.snippet(i.toString()) //Assign index for marker

                                mMap!!.addMarker(markerOptions) //Add marker to map

                                //Move camera
                                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                                mMap!!.animateCamera(CameraUpdateFactory.zoomTo(10f))

                            }


                        }
                    }

                })

    }

    fun getUrl(latitude: Double, longitude: Double, typePlace: String): String {
        //Toast.makeText(applicationContext, "latitude: $latitude \n longitude : $longitude", Toast.LENGTH_LONG).show()
        val googlePlaceUrl = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
        googlePlaceUrl.append("?location=$latitude,$longitude")
        googlePlaceUrl.append("&radius=10000") //10 km
        googlePlaceUrl.append("&type=$typePlace")
        googlePlaceUrl.append("&key=AIzaSyC3WmR6rsB26GfTw0WgGkRMrDRvEXB-Gyw")

        //Log.d("URL_DEBUG", googlePlaceUrl.toString())
        return googlePlaceUrl.toString()
    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                mLastLocation = p0!!.lastLocation //get last location

                if(mMarker != null)
                {
                    mMarker!!.remove()
                }

                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                val latLng = LatLng(latitude,longitude)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("Votre position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                mMarker = mMap.addMarker(markerOptions)

                //move camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))

                nearByPlace(intent.getStringExtra("CHOIX"))
                //Toast.makeText(applicationContext, "latitude: $latitude \n longitude : $longitude", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f

        //Toast.makeText(applicationContext, "latitude: $latitude \n longitude : $longitude", Toast.LENGTH_LONG).show()
    }

    fun checkLocationPermission(): Boolean {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_CODE)
            else
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_CODE)
            return false
        }
        else
        {
            return true
        }
    }

    //override onRequestPermissionResult
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode)
        {
            MY_PERMISSION_CODE->{
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        if(checkLocationPermission())
                        {
                            buildLocationRequest()
                            buildLocationCallBack()

                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

                            mMap.isMyLocationEnabled=true
                        }
                }
                else
                {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //enabled zoom control
        mMap.uiSettings.isZoomControlsEnabled=true

        //Init google play services
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                mMap.isMyLocationEnabled = true
            }
        }
        else
            mMap.isMyLocationEnabled = true

        mMap!!.setOnMarkerClickListener { marker ->
            if(marker.snippet != null) {
                Common.currentResult = currentPlace!!.results!![Integer.parseInt(marker.snippet)]

                startActivity(Intent(this@MapActivity, ViewPlace::class.java))
            }
            true
        }
    }

}
