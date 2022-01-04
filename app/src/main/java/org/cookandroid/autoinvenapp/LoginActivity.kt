package org.cookandroid.autoinvenapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.cookandroid.autoinvenapp.objects.PrefObject
import retrofit2.*

class LoginActivity : AppCompatActivity() {
    lateinit var login: Button
    lateinit var idtext: EditText
    lateinit var pwtext: EditText
    lateinit var autologin: CheckBox

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        val masterKey = MasterKey.Builder(
            this@LoginActivity,
            MasterKey.DEFAULT_MASTER_KEY_ALIAS
        ).setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        PrefObject.prefs = EncryptedSharedPreferences.create(
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

        if (PrefObject.prefs.getBoolean("ischecked", false)) {
            var intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        login.setOnClickListener {
            if(autologin.isChecked){
                PrefObject.editor.putBoolean("ischecked",true)
                PrefObject.editor.apply()
            }
            PrefObject.sendLoginApi(idtext.text.toString(), pwtext.text.toString(),this@LoginActivity)
        }

    }
}