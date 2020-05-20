package com.example.finder.Common

import com.example.finder.Model.Results
import com.example.finder.Remote.IGoogleAPIService
import com.example.finder.Remote.RetrofitClient
import com.example.finder.Remote.RetrofitScalarsClient

object Common {
    private val GOOGLE_API_URL="https://maps.googleapis.com/"

    var currentResult: Results?=null

    val googleApiService:IGoogleAPIService
        get()= RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)

    val googleApiServiceScalars:IGoogleAPIService
        get()= RetrofitScalarsClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)
}