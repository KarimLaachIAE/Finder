package com.example.finder

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.finder.Common.Common
import com.example.finder.Model.PlaceDetail
import com.example.finder.Remote.IGoogleAPIService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_place.*

class ViewPlace : AppCompatActivity() {

    internal lateinit var mService:IGoogleAPIService
    var mPlace: PlaceDetail?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_place)

        mService = Common.googleApiService

        place_name.text = Common.currentResult!!.name
        place_adress.text = Common.currentResult!!.vicinity


        if(Common.currentResult!!.opening_hours != null && Common.currentResult!!.opening_hours!!.open_now){
            place_open_hour.text = "Actuellement ouvert"
        }else{
            place_open_hour.text = "Actuellement fermé"
        }


        /*btn_show_map.setOnClickListener {
            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mPlace!!.result!!.url))
            startActivity(mapIntent)
        }*/

        btn_view_direction.setOnClickListener {
            val viewDirections = Intent(this@ViewPlace, ViewDirections::class.java)
            startActivity(viewDirections)
        }

        if(Common.currentResult!!.photos != null && Common.currentResult!!.photos!!.size > 0)
            Picasso.with(this)
                .load(getPhotoOfPlace(Common.currentResult!!.photos!![0].photo_reference!!, 1000))
                .into(photo)

        if(Common.currentResult!!.rating != null)
            rating_bar.rating = Common.currentResult!!.rating.toFloat()
        else
            rating_bar.visibility = View.GONE

    }

    fun getPlaceDetailUrl(place_id: String): String{
        val url = StringBuilder("https://maps.googlesapis.com/maps/api/place/details/json")
        url.append("?placeid=$place_id")
        url.append("&key=AIzaSyC3WmR6rsB26GfTw0WgGkRMrDRvEXB-Gyw")
        return url.toString()
    }

    fun getPhotoOfPlace(photo_reference: String, maxWidth: Int): String {
        val url = StringBuilder("https://maps.googleapis.com/maps/api/place/photo")
        url.append("?maxwidth=$maxWidth")
        url.append("&photoreference=$photo_reference")
        url.append("&key=AIzaSyC3WmR6rsB26GfTw0WgGkRMrDRvEXB-Gyw")
        return url.toString()
    }
}
