package com.example.finder

import junit.framework.Assert.*
import org.junit.Test

class MyTest {
    val clsMA = MapActivity()
    val clsVD = ViewDirections()
    val clsVP = ViewPlace()

    @Test
    fun testGetUrl(){
        assertEquals(
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=1.1,1.1&radius=10000&type=test&key=AIzaSyC3WmR6rsB26GfTw0WgGkRMrDRvEXB-Gyw",
             clsMA.getUrl(1.1, 1.1, "test"))
    }

    @Test
    fun testPermission(){
        assertTrue(clsMA.checkLocationPermission())
        assertTrue(clsVD.checkLocationPermission())
    }

    @Test
    fun testPlaceDetailUrl(){
        assertEquals(
            "https://maps.googlesapis.com/maps/api/place/details/json?placeid=test&key=AIzaSyC3WmR6rsB26GfTw0WgGkRMrDRvEXB-Gyw",
            clsVP.getPlaceDetailUrl("test")
        )
    }

    @Test
    fun testGetPhotoOfPlace(){
        assertEquals(
            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1&photoreference=test&key=AIzaSyC3WmR6rsB26GfTw0WgGkRMrDRvEXB-Gyw",
            clsVP.getPhotoOfPlace("test", 1))
    }
}