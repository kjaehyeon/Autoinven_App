package org.cookandroid.autoinvenapp.objects

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.cookandroid.autoinvenapp.api.LoginAPI
import org.cookandroid.autoinvenapp.data.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PrefObject {
    lateinit var prefs: SharedPreferences

    fun sendLoginApi(id: String, pw: String, context: Context) {
        val masterKey = MasterKey.Builder(
            context,
            MasterKey.DEFAULT_MASTER_KEY_ALIAS
        ).setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        prefs = EncryptedSharedPreferences.create(
            context,
            "userinfo",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val BASE_URL = "http://192.168.0.143:5000/"
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(LoginAPI::class.java)
        val editor = prefs.edit()
        val callPostLogin = api.postLogin(id, pw)
        Log.d("test", "$id $pw")
        callPostLogin.enqueue(object : Callback<Request> {
            override fun onResponse(
                call: Call<Request>,
                response: Response<Request>
            ) {
                if (response.isSuccessful) {
                    editor.putString("id", id)
                    editor.putString("pw", pw)
                    editor.putString("token", response.body()?.token)
                    editor.apply()
                } else {
                    when (response.code()) {
                        401 -> {
                            AlertDialog.Builder(context)
                                .setTitle("Message") //제목
                                .setMessage("아이디와 비밀번호를 확인해주세요.") // 메시지
                                .setPositiveButton("닫기", null)
                                .show()
                        }
                        500 -> {
                            AlertDialog.Builder(context)
                                .setTitle("Message") //제목
                                .setMessage("아이디와 비밀번호를 확인해주세요.") // 메시지
                                .setPositiveButton("닫기", null)
                                .show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Request>, t: Throwable) {
                t.stackTrace
            }
        })
    }
}