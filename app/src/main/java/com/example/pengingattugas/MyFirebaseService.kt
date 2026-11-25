package com.example.pengingattugas

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// MyFirebaseService.kt
class MyFirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // Kirim token ini ke server Anda, lalu simpan di database
        Log.d(TAG, "Token baru: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        // Studi kasus memakai data message: judul, isi, dan tugas_id
        val judul = message.data[KEY_JUDUL] ?: "Pengingat"
        val isi = message.data[KEY_ISI].orEmpty()
        val tugasId = message.data[KEY_TUGAS_ID]
        tampilkanNotifikasi(judul, isi, tugasId)
    }

    private fun tampilkanNotifikasi(judul: String, isi: String, tugasId: String?) {
        val manager = getSystemService(NotificationManager::class.java)
        buatChannelJikaBelum(manager)

        // Id unik per notifikasi, dipakai juga sebagai requestCode PendingIntent
        // supaya setiap notifikasi membawa tugas_id miliknya sendiri
        val notifikasiId = System.currentTimeMillis().toInt()

        // Saat diketuk, MainActivity terbuka dan menerima tugas_id
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_TUGAS_ID, tugasId)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, notifikasiId, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notifikasi = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifikasi)
            .setContentTitle(judul)
            .setContentText(isi)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(notifikasiId, notifikasi)
    }

    // Channel wajib dibuat sejak Android 8.0 (API 26)
    private fun buatChannelJikaBelum(manager: NotificationManager) {
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Pengingat Tugas",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }

    private companion object {
        const val TAG = "FCM"
        const val CHANNEL_ID = "pengingat_tugas"
        const val KEY_JUDUL = "judul"
        const val KEY_ISI = "isi"
        const val KEY_TUGAS_ID = "tugas_id"
    }
}
