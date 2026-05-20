package com.weathersnap.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageCompressor @Inject constructor() {

    data class CompressionResult(
        val compressedPath: String,
        val originalSizeBytes: Long,
        val compressedSizeBytes: Long
    )

    fun compressImage(
        context: Context,
        originalPath: String,
        quality: Int = 70,
        maxWidth: Int = 1280,
        maxHeight: Int = 960
    ): CompressionResult {
        val originalFile = File(originalPath)
        val originalSize = originalFile.length()

        // Decode with inSampleSize for memory efficiency
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(originalPath, options)

        val sampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
        options.apply {
            inJustDecodeBounds = false
            inSampleSize = sampleSize
        }

        val bitmap = BitmapFactory.decodeFile(originalPath, options)
            ?: throw IllegalStateException("Failed to decode image")

        // Scale down if still too large
        val scaledBitmap = scaleBitmap(bitmap, maxWidth, maxHeight)

        // Save compressed image
        val compressedFile = File(
            context.cacheDir,
            "compressed_${System.currentTimeMillis()}.jpg"
        )
        FileOutputStream(compressedFile).use { out ->
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }

        if (scaledBitmap != bitmap) {
            scaledBitmap.recycle()
        }
        bitmap.recycle()

        return CompressionResult(
            compressedPath = compressedFile.absolutePath,
            originalSizeBytes = originalSize,
            compressedSizeBytes = compressedFile.length()
        )
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height, width) = options.outHeight to options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxWidth && height <= maxHeight) return bitmap

        val ratio = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
