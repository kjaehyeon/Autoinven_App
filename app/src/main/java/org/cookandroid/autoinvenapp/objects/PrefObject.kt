package org.cookandroid.autoinvenapp.objects

import LoadingActivity
import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.cookandroid.autoinvenapp.LoginActivity
import org.cookandroid.autoinvenapp.MainActivity
import org.cookandroid.autoinvenapp.api.LoginAPI
import org.cookandroid.autoinvenapp.data.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.app.ProgressDialog
import android.content.DialogInterface


object PrefObject {
    lateinit var prefs: SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    lateinit var loginActivity : LoginActivity

    fun sendLoginApi(id: String, pw: String, context: Context) {
        Log.d("test","in sendLoginApi")
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
        loginActivity = context as LoginActivity
        val BASE_URL = "http://192.168.0.143:5000/"
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(LoginAPI::class.java)
        editor = prefs.edit()
        val dialog = LoadingActivity(context)
        dialog.show()

        val callPostLogin = api.postLogin(id, pw)
        callPostLogin.enqueue(object : Callback<Request> {
            override fun onResponse(
                call: Call<Request>,
                response: Response<Request>
            ) {
                if (response.isSuccessful) {
                    editor.remove("token")
                    editor.putString("id", id)
                    editor.putString("pw", pw)
                    editor.putString("token", response.body()?.token)
                    Log.d("test","Received token : "+response.body()?.token)
                    editor.apply()
                    if(context == loginActivity) {
                        var intent = Intent(context, MainActivity::class.java)
                        startActivity(context, intent, null)
                        dialog.dismiss()
                        //loginActivity.finish()
                    }

                } else {
                    when (response.code()) {
                        400 -> {
                            if(context == loginActivity){
                                AlertDialog.Builder(context)
                                    .setTitle("Message") //제목
                                    .setMessage("아이디와 비밀번호를 확인해주세요.") // 메시지
                                    .setNegativeButton("닫기", null)
                                    .show()
                                dialog.dismiss()
                            }
                        }
                        500 -> {
                            if(context == loginActivity){
                                AlertDialog.Builder(context)
                                    .setTitle("Message") //제목
                                    .setMessage("잠시 후 다시 시도해주세요") // 메시지
                                    .setNegativeButton("닫기", null)
                                    .show()
                                dialog.dismiss()
                            }
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