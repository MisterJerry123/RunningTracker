package com.misterjerry.runningtracker.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.osmdroid.util.GeoPoint
import java.io.ByteArrayOutputStream

class Converters {

    @TypeConverter
    fun toBitmap(bytes: ByteArray?): Bitmap? {
        return bytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
    }

    @TypeConverter
    fun fromBitmap(bmp: Bitmap?): ByteArray? {
        val outputStream = ByteArrayOutputStream()
        bmp?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
    
    @TypeConverter
    fun fromPathPoints(pathPoints: List<List<GeoPoint>>): String {
        // Convert to a simple list of list of arrays [lat, lon] to avoid issues with GeoPoint structure
        val simpleList = pathPoints.map { polyline ->
            polyline.map { point -> listOf(point.latitude, point.longitude) }
        }
        return Gson().toJson(simpleList)
    }

    @TypeConverter
    fun toPathPoints(json: String): List<List<GeoPoint>> {
        val type = object : TypeToken<List<List<List<Double>>>>() {}.type
        val simpleList: List<List<List<Double>>> = Gson().fromJson(json, type)
        return simpleList.map { polyline ->
            polyline.map { coords -> GeoPoint(coords[0], coords[1]) }
        }
    }
}
