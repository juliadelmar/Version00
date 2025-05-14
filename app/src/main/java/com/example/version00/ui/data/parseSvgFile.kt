package com.example.version00.ui.data

// En tu archivo donde tienes parseSvgFile (o un archivo de utilidades)

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposePath
import androidx.core.graphics.PathParser
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import android.graphics.Path as AndroidPath

// Modelo para un path parseado (sin cambios)
data class SvgPart(
    val id: String, // Seguiremos llamándolo 'id' en nuestro modelo por conveniencia
    val composePath: Path
)

// Función para parsear el Vector Drawable
fun parseVectorDrawableFile(context: Context, resourceId: Int): List<SvgPart> {
    val parts = mutableListOf<SvgPart>()
    val parser: XmlPullParser = context.resources.getXml(resourceId) // Más directo para XML de recursos

    try {
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (parser.name.equals("path", ignoreCase = true)) {
                        // Buscar el atributo android:name
                        // El namespace para 'android:' es "http://schemas.android.com/apk/res/android"
                        val name = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "name")
                        val pathData = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "pathData")

                        if (name != null && pathData != null) {
                            try {
                                val androidPath: AndroidPath? = PathParser.createPathFromPathData(pathData)
                                if (androidPath != null) {
                                    parts.add(SvgPart(name, androidPath.asComposePath()))
                                    Log.d("VectorParser", "Parsed path: name='${name}'")
                                } else {
                                    Log.w("VectorParser", "Could not parse pathData for name: $name")
                                }
                            } catch (e: Exception) { // Puede ser IllegalArgumentException de PathParser
                                Log.e("VectorParser", "Error parsing pathData for name: $name, pathData: $pathData", e)
                            }
                        } else {
                            if (name == null) Log.w("VectorParser", "Path found without android:name")
                            if (pathData == null) Log.w("VectorParser", "Path found without android:pathData")
                        }
                    }
                }
            }
            eventType = parser.next()
        }
    } catch (e: XmlPullParserException) {
        Log.e("VectorParser", "XML Parsing Error", e)
    } catch (e: IOException) {
        Log.e("VectorParser", "IO Error reading vector drawable", e)
    }
    Log.d("VectorParser", "Total parts parsed: ${parts.size}")
    return parts
}