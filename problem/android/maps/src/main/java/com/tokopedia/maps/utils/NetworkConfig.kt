package com.tokopedia.maps

import com.tokopedia.maps.model.CountryResponse
import com.tokopedia.maps.utils.Config.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class NetworkConfig {
    fun getInterceptor() : OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
                .addInterceptor(logging)
                .addNetworkInterceptor { chain ->
                    val requestBuilder: Request.Builder = chain.request().newBuilder()
                    requestBuilder.header("x-rapidapi-key", "2ec6df9dabmsh6d415a9cbc0d6e0p164551jsn1e3340a82dda")
                    requestBuilder.header("x-rapidapi-host", "geo-services-by-mvpc-com.p.rapidapi.com")
                    chain.proceed(requestBuilder.build())
                }
                .build()


    }
    fun getRetrofit() : Retrofit {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getInterceptor())

                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
    fun getService() = getRetrofit().create(CountryInterface::class.java)
}

interface CountryInterface {
    @GET("countries?language=en")
    fun getCountryDetail(@Query("countrycode") locale: String): Call<CountryResponse>
}