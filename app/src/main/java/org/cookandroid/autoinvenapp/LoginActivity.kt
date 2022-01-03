package org.cookandroid.autoinvenapp

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.cookandroid.autoinvenapp.api.LoginAPI
import org.cookandroid.autoinvenapp.data.Request
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
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
        val masterKey = MasterKey.Builder(this,
        MasterKey.DEFAULT_MASTER_KEY_ALIAS).setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        prefs = EncryptedSharedPreferences.create( this,
        "userinfo",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login = findViewById<Button>(R.id.login)
        idtext = findViewById<EditText>(R.id.id)
        pwtext = findViewById<EditText>(R.id.pw)
        autologin = findViewById<CheckBox>(R.id.autologin)
        val test = findViewById<Button>(R.id.test)

        val editor = prefs.edit()

        if(prefs.getBoolean("ischecked",false)){
            var intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }

        test.setOnClickListener {
            if(autologin.isChecked) {
                editor.putBoolean("ischecked",true)
            }
            else
                editor.putBoolean("ischecked",false)

            editor.putString("id", idtext.text.toString())
            editor.putString("pw", pwtext.text.toString())
            editor.apply()
            Log.d("test", idtext.text.toString())
            var intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }

        login.setOnClickListener {
            val callPostLogin = api.postLogin(idtext.text.toString(), pwtext.text.toString())
            callPostLogin.enqueue(object : Callback<Request> {
                override fun onResponse(
                    call: Call<Request>,
                    response: Response<Request>
                ) {
                    if (response.isSuccessful) {
                        if(autologin.isChecked) {
                            editor.putBoolean("ischecked",true)
                        }
                        editor.putString("id", idtext.text.toString())
                        editor.putString("pw", pwtext.text.toString())
                        editor.apply()
                        Log.d("test", "Success=================")
                        var intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)

                    } else {
                        when (response.code()) {
                            400 -> {
                                Log.d("test", "fail=================")
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