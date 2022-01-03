package org.cookandroid.autoinvenapp

import android.app.AlertDialog
import android.content.Context
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
                                val dlg: AlertDialog.Builder =
                                    AlertDialog.Builder(context)
                                dlg.setTitle("Message") //제목
                                    .setMessage("아이디와 비밀번호를 확인해주세요.") // 메시지
                                    .setPositiveButton("닫기", null)
                                    .show()
                            }
                            500 -> {
                                val dlg: AlertDialog.Builder =
                                    AlertDialog.Builder(context)
                                dlg.setTitle("Message") //제목
                                    .setMessage("죄송합니다. 다시 시도해 주세요.") // 메시지
                                    .setPositiveButton("닫기", null)
                                    .show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Request>, t: Throwable) {
                    t.stackTrace
                    Log.d("test", t.message.toString())
                    Log.d("test", "fail")
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        val masterKey = MasterKey.Builder(
            this@LoginActivity,
            MasterKey.DEFAULT_MASTER_KEY_ALIAS
        ).setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        prefs = EncryptedSharedPreferences.create(
            this@LoginActivity,
            "userinfo",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login = findViewById<Button>(R.id.login)
        idtext = findViewById<EditText>(R.id.id)
        pwtext = findViewById<EditText>(R.id.pw)
        autologin = findViewById<CheckBox>(R.id.autologin)

        val editor = prefs.edit()
        if (prefs.getBoolean("ischecked", false)) {
            var intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
        login.setOnClickListener {
            if(autologin.isChecked){
                editor.putBoolean("ischecked",true)
                editor.apply()
            }

            sendLoginApi(idtext.text.toString(), pwtext.text.toString(), this@LoginActivity)
            var intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}