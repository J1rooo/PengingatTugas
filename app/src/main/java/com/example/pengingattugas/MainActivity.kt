package com.example.pengingattugas

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging

// MainActivity.kt
class MainActivity : AppCompatActivity() {

    private lateinit var txtStatus: TextView

    // Harus berupa properti kelas, bukan di dalam onCreate
    private val mintaIzin = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { diberikan ->
        if (!diberikan) Toast.makeText(this, "Notifikasi dimatikan", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtStatus = findViewById(R.id.txtStatus)

        mintaIzinNotifikasi()
        ambilTokenFcm()
        tampilkanStatus(intent)
    }

    // Dipanggil saat notifikasi diketuk ketika MainActivity sudah terbuka
    // (karena FLAG_ACTIVITY_SINGLE_TOP), sehingga onCreate tidak dijalankan lagi
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        tampilkanStatus(intent)
    }

    // Android 13 (API 33) ke atas wajib meminta izin notifikasi
    private fun mintaIzinNotifikasi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mintaIzin.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Token ditampilkan di Logcat (filter: FCM) untuk dipakai saat uji kirim pesan
    private fun ambilTokenFcm() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Token: ${task.result}")
            } else {
                Log.w(TAG, "Gagal mengambil token", task.exception)
            }
        }
    }

    // Tampilkan tugas dari notifikasi yang diketuk, di TextView txtStatus
    private fun tampilkanStatus(intent: Intent) {
        val tugasId = intent.getStringExtra(EXTRA_TUGAS_ID)
        txtStatus.text = if (tugasId != null) "Buka tugas $tugasId" else "Menunggu pengingat tugas"
    }

    companion object {
        const val EXTRA_TUGAS_ID = "tugas_id"
        private const val TAG = "FCM"
    }
}
