package com.example.clocker.Firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.clocker.MainActivity
import com.example.clocker.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FCMService"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.title ?: "Clocker", it.body ?: "Nueva notificación")
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .update("fcmToken", token)
                .addOnSuccessListener {
                    Log.d(TAG, "✅ Token guardado en Firestore")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "❌ Error guardando token", e)
                }
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val type = data["type"]
        val title = data["title"] ?: "Notificación"
        val body = data["body"] ?: ""

        when (type) {
            "new_clock" -> sendNotification(title, body, "CLOCK_CHANNEL")
            "report_ready" -> sendNotification(title, body, "REPORT_CHANNEL")
            "reminder" -> sendNotification(title, body, "REMINDER_CHANNEL")
            else -> sendNotification(title, body)
        }
    }

    private fun sendNotification(
        title: String,
        messageBody: String,
        channelId: String = "DEFAULT_CHANNEL"
    ) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_report)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    "DEFAULT_CHANNEL",
                    "Notificaciones Generales",
                    NotificationManager.IMPORTANCE_DEFAULT
                ),
                NotificationChannel(
                    "CLOCK_CHANNEL",
                    "Marcas de Asistencia",
                    NotificationManager.IMPORTANCE_HIGH
                ),
                NotificationChannel(
                    "REPORT_CHANNEL",
                    "Reportes",
                    NotificationManager.IMPORTANCE_DEFAULT
                ),
                NotificationChannel(
                    "REMINDER_CHANNEL",
                    "Recordatorios",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )

            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}