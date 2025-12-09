package Data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object FirebaseConverters {

    // --- 1. IMÁGENES (Vital para FirebaseDataManager) ---
    fun bitmapToString(bitmap: Bitmap?): String {
        if (bitmap == null) return ""
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun stringToBitmap(encodedString: String): Bitmap? {
        return try {
            val decodedByte = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
        } catch (e: Exception) {
            null
        }
    }

    // --- 2. HORAS (Vital para ZoneActivity) ---
    fun timeToString(time: LocalTime?): String {
        return time?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "00:00"
    }

    fun stringToTime(timeString: String): LocalTime {
        return try {
            LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            LocalTime.MIN
        }
    }

    // --- 3. FECHAS (Vital para ClockActivity/Listas) ---
    // ¡Es muy probable que falte esto y cause el cierre!
    fun dateTimeToString(dateTime: java.time.LocalDateTime?): String {
        return dateTime?.toString() ?: ""
    }

    fun stringToDateTime(dateTimeString: String): java.time.LocalDateTime {
        return try {
            if (dateTimeString.isNotEmpty())
                java.time.LocalDateTime.parse(dateTimeString)
            else
                java.time.LocalDateTime.now()
        } catch (e: Exception) {
            java.time.LocalDateTime.now()
        }
    }
}