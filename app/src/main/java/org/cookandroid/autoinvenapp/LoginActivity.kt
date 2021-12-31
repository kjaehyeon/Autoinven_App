package org.cookandroid.autoinvenapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import org.cookandroid.autoinvenapp.api.LoginAPI
import org.cookandroid.autoinvenapp.data.Request
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    lateinit var masterKeyAlias: String
    lateinit var login: Button
    lateinit var idtext: EditText
    lateinit var pwtext: EditText
    lateinit var autologin: CheckBox
    companion object {
        lateinit var prefs : SharedPreferences
    }

    val BASE_URL= "http://192.168.0.17:4000/"
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val api = retrofit.create(LoginAPI::class.java)
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        prefs = EncryptedSharedPreferences.create( "userinfo",
            masterKeyAlias,
            this@LoginActivity,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM )

        prefs = getSharedPreferences("userinfo", 0)
        val editor = prefs.edit()
        val id = idtext.text.toString()
        val pw = pwtext.text.toString()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login = findViewById<Button>(R.id.login)
        idtext = findViewById<EditText>(R.id.id)
        pwtext = findViewById<EditText>(R.id.pw)
        autologin = findViewById<CheckBox>(R.id.autologin)


        login.setOnClickListener {
            val callPostLogin = api.postLogin(idtext.text.toString(), pwtext.text.toString())
            callPostLogin.enqueue(object : Callback<Request> {
                override fun onResponse(
                    call: Call<Request>,
                    response: Response<Request>
                ) {
                    if (response.isSuccessful) {
                        editor.putString("id", id)
                        editor.putString("pw", pw)
                        editor.apply()
                        Log.d("test", "Success=================")
                        var intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        when (response.code()) {
                            400 -> {
                                val dlg: AlertDialog.Builder =
                                    AlertDialog.Builder(this@LoginActivity)
                                dlg.setTitle("Message") //제목
                                dlg.setMessage("아이디와 비밀번호를 확인해주세요.") // 메시지
                                dlg.setPositiveButton("닫기", null)
                                dlg.show()
                            }
                            500 -> {
                                val dlg: AlertDialog.Builder =
                                    AlertDialog.Builder(this@LoginActivity)
                                dlg.setTitle("Message") //제목
                                dlg.setMessage("죄송합니다. 다시 시도해 주세요.") // 메시지
                                dlg.setPositiveButton("닫기", null)
                                dlg.show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Request>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }
    }
}