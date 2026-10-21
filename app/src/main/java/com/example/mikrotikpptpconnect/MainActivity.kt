package com.example.mikrotikpptpconnect

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.VpnService
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var etServer: TextInputEditText
    private lateinit var etPort: TextInputEditText
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etIpsecSecret: TextInputEditText
    private lateinit var tvStatus: TextView
    private lateinit var btnSave: Button
    private lateinit var btnConnect: Button
    private lateinit var btnDisconnect: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("mikrotik_profile", Context.MODE_PRIVATE)

        etServer = findViewById(R.id.etServer)
        etPort = findViewById(R.id.etPort)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etIpsecSecret = findViewById(R.id.etIpsecSecret)
        tvStatus = findViewById(R.id.tvStatus)
        btnSave = findViewById(R.id.btnSave)
        btnConnect = findViewById(R.id.btnConnect)
        btnDisconnect = findViewById(R.id.btnDisconnect)

        loadProfile()

        btnSave.setOnClickListener { saveProfile() }
        btnConnect.setOnClickListener { connect() }
        btnDisconnect.setOnClickListener {
            setStatus(getString(R.string.status_disconnected))
        }
    }

    private fun loadProfile() {
        etServer.setText(prefs.getString("server", ""))
        etPort.setText(prefs.getString("port", "1701"))
        etUsername.setText(prefs.getString("username", ""))
        etPassword.setText(prefs.getString("password", ""))
        etIpsecSecret.setText(prefs.getString("ipsec_secret", ""))
    }

    private fun saveProfile() {
        prefs.edit()
            .putString("server", etServer.text.toString().trim())
            .putString("port", etPort.text.toString().trim())
            .putString("username", etUsername.text.toString().trim())
            .putString("password", etPassword.text.toString())
            .putString("ipsec_secret", etIpsecSecret.text.toString())
            .apply()
        Toast.makeText(this, R.string.profile_saved, Toast.LENGTH_SHORT).show()
    }

    private fun connect() {
        val server = etServer.text.toString().trim()
        val port = etPort.text.toString().trim().toIntOrNull() ?: 1701
        val user = etUsername.text.toString().trim()
        val pass = etPassword.text.toString()
        val ipsec = etIpsecSecret.text.toString()

        if (server.isEmpty() || user.isEmpty()) {
            Toast.makeText(this, R.string.error_missing_fields, Toast.LENGTH_LONG).show()
            return
        }

        // Hand off the L2TP/IPsec profile to the system VPN chooser.
        // strongSwan (org.strongswan.android) handles IKEv2/L2TP-IPsec on Android.
        val intent = Intent("org.strongswan.android.CONNECTIVITY_ACTION")
        intent.setPackage("org.strongswan.android")
        intent.putExtra("server", server)
        intent.putExtra("port", port)
        intent.putExtra("username", user)
        intent.putExtra("password", pass)
        intent.putExtra("ipsec_secret", ipsec)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            startActivity(intent)
            setStatus(getString(R.string.status_connecting))
        } catch (e: Exception) {
            Toast.makeText(
                this,
                getString(R.string.strongswan_missing),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setStatus(text: String) {
        tvStatus.text = getString(R.string.status_label, text)
    }
}
