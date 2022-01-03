package org.cookandroid.autoinvenapp.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.cookandroid.autoinvenapp.LoginActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object ApiClient {
    private const val BASE_URL = "http://192.168.0.143:5000/"
    fun getApiClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient(AppInterceptor()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
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
                .addHeader("token", LoginActivity.prefs.getString("token", "ERROR")!!)
                .build()

            proceed(newRequest)
        }
    }
}