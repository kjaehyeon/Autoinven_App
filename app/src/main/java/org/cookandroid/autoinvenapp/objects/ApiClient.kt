package org.cookandroid.autoinvenapp.objects

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object ApiClient {

    const val BASE_URL = "http://192.168.0.18:5000"
    //private const val BASE_URL = "http://192.168.0.17:5000"

    fun getApiClient(withToken : Boolean = true): Retrofit {
        return if(withToken){
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(provideOkHttpClient(AppInterceptor()))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }else{
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient
        = OkHttpClient.Builder().run {
            addInterceptor(interceptor)
            build()
        }

    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain) : Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("token", PrefObject.prefs.getString("token", "ERROR")!!)
                .build()
            proceed(newRequest)
        }
    }
}