package com.example.clocker.Firebase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class FirebaseStorageManager {

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference
    private val TAG = "FirebaseStorageManager"

    suspend fun subirFotoClock(
        idClock: String,
        bitmap: Bitmap,
        compressQuality: Int = 80
    ): Result<String> {
        return try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, baos)
            val data = baos.toByteArray()

            val photoRef = storageRef.child("clock_photos/$idClock.jpg")
            photoRef.putBytes(data).await()

            val downloadUrl = photoRef.downloadUrl.await()

            Log.d(TAG, "✅ Foto subida: $downloadUrl")
            Result.success(downloadUrl.toString())

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error subiendo foto", e)
            Result.failure(e)
        }
    }

    suspend fun descargarFotoClock(photoUrl: String): Result<Bitmap> {
        return try {
            val photoRef = storage.getReferenceFromUrl(photoUrl)
            val maxBytes: Long = 5 * 1024 * 1024
            val bytes = photoRef.getBytes(maxBytes).await()
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            Log.d(TAG, "✅ Foto descargada")
            Result.success(bitmap)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error descargando foto", e)
            Result.failure(e)
        }
    }

    suspend fun eliminarFotoClock(photoUrl: String): Result<Unit> {
        return try {
            val photoRef = storage.getReferenceFromUrl(photoUrl)
            photoRef.delete().await()

            Log.d(TAG, "✅ Foto eliminada")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error eliminando foto", e)
            Result.failure(e)
        }
    }

    suspend fun listarFotosClocks(): Result<List<String>> {
        return try {
            val listResult = storageRef.child("clock_photos").listAll().await()

            val urls = listResult.items.map { item ->
                item.downloadUrl.await().toString()
            }

            Log.d(TAG, "✅ ${urls.size} fotos listadas")
            Result.success(urls)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error listando fotos", e)
            Result.failure(e)
        }
    }
}