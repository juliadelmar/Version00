package com.example.version00.ui.data

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposePath
import androidx.core.graphics.PathParser
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import android.graphics.Path as AndroidPath

/**
 * Modelo que representa una parte de un VectorDrawable parseado.
 *
 * @property id Nombre o identificador del path (extraído del atributo android:name).
 * @property composePath Path convertido a formato Compose (Path) para renderizado.
 */
data class SvgPart(
    val id: String,
    val composePath: Path
)

/**
 * Parsea un recurso XML de tipo VectorDrawable (.xml en res/drawable) y extrae
 * todos los elementos `<path>` con sus atributos `android:name` y `android:pathData`.
 *
 * Devuelve una lista de objetos [SvgPart] que pueden ser usados en una vista personalizada.
 *
 * @param context Contexto de la app.
 * @param resourceId ID del recurso XML vectorial a analizar (ej. R.drawable.cuerpo_vector).
 * @return Lista de [SvgPart] con ID y Path de cada elemento encontrado.
 */
fun parseVectorDrawableFile(context: Context, resourceId: Int): List<SvgPart> {
    val parts = mutableListOf<SvgPart>()
    val parser: XmlPullParser = context.resources.getXml(resourceId)

    try {
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.name.equals("path", ignoreCase = true)) {
                val name = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "name")
                val pathData = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "pathData")

                if (name != null && pathData != null) {
                    try {
                        val androidPath: AndroidPath? = PathParser.createPathFromPathData(pathData)
                        if (androidPath != null) {
                            parts.add(SvgPart(name, androidPath.asComposePath()))
                            Log.d("VectorParser", "Parsed path: name='$name'")
                        } else {
                            Log.w("VectorParser", "Could not parse pathData for name: $name")
                        }
                    } catch (e: Exception) {
                        Log.e("VectorParser", "Error parsing pathData for name: $name", e)
                    }
                } else {
                    if (name == null) Log.w("VectorParser", "Path found without android:name")
                    if (pathData == null) Log.w("VectorParser", "Path found without android:pathData")
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
