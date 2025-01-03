import android.content.Context
import com.arnatech.jadwalshalat.R
import org.json.JSONArray

fun getHadis(context: Context): List<String> {
    val inputStream = context.resources.openRawResource(R.raw.hadis)
    val json = inputStream.bufferedReader().use { it.readText() }
    val jsonArray = JSONArray(json)

    val hadisList = mutableListOf<String>()
    for (i in 0 until jsonArray.length()) {
        val hadis = jsonArray.getJSONObject(i).getString("text")
        hadisList.add(hadis)
    }
    return hadisList
}
