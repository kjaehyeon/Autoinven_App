package org.cookandroid.autoinvenapp.objects

import LoadingActivity
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
import org.cookandroid.autoinvenapp.data.LoginInfo
import org.cookandroid.autoinvenapp.data.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object PrefObject {
    lateinit var prefs: SharedPreferences
    lateinit var editor : SharedPreferences.Editor

    fun sendLoginApi(email: String, pw: String, context: Context) {
        Log.d("test","in sendLoginApi")
        val masterKey = MasterKey.Builder(
            context,
            MasterKey.DEFAULT_MASTER_KEY_ALIAS
        ).setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val api = ApiClient.getApiClient(withToken = false).create(LoginAPI::class.java)
        val dialog = LoadingActivity(context)

        prefs = EncryptedSharedPreferences.create(
            context,
            "userinfo",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        editor = prefs.edit()

        if(context is LoginActivity) dialog.show()
        val callPostLogin = api.postLogin(LoginInfo(email, pw))

        callPostLogin.enqueue(object : Callback<Request> {
            override fun onResponse(
                call: Call<Request>,
                response: Response<Request>
            ) {
                if (response.isSuccessful) {
                    editor.putString("id", email)
                    editor.putString("pw", pw)
                    editor.putString("token", response.body()?.token)
                    Log.d("test","Received token : "+response.body()?.token)
                    editor.apply()
                    if(context is LoginActivity) {
                        startActivity(context, Intent(context, MainActivity::class.java), null)
                        context.finish()
                        dialog.dismiss()
                    }
                } else {
                    when (response.code()) {
                        401 -> {
                            if(context is LoginActivity){
                                AlertDialog.Builder(context)
                                    .setTitle("Message") //제목
                                    .setMessage("아이디와 비밀번호를 확인해주세요.") // 메시지
                                    .setNegativeButton("닫기", null)
                                    .show()
                                dialog.dismiss()
                            }
                            else if(context is MainActivity){
                                AlertDialog.Builder(context)
                                    .setTitle("Message")
                                    .setMessage("비밀번호가 변경되었습니다. 다시 로그인 해주세요.")
                                    .setNegativeButton("닫기",null)
                                    .show()
                                context.finish()
                                startActivity(context, Intent(context, LoginActivity::class.java), null)
                            }
                        }
                        500 -> {
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
            override fun onFailure(call: Call<Request>, t: Throwable) {
                t.stackTrace
                AlertDialog.Builder(context)
                    .setTitle("Message") //제목
                    .setMessage("서버 오류가 발생하였습니다.\n잠시 후 다시 시도해주세요") // 메시지
                    .setNegativeButton("닫기", null)
                    .show()
                dialog.dismiss()
            }
        })
    }

}