import com.google.gson.*
import kotlinx.coroutines.*

private fun fetchJson(term: String): String {
    val url = "https://en.wikipedia.org/w/api.php" +
            "?action=query&format=json&prop=extracts" +
            "&exsectionformat=plain&exsentences=2&explaintext=1" +
            "&titles=$term"
    return java.net.URL(url).readText()
}

private suspend fun parseExtract(wikipediaJson: Deferred<String>): String {
    val jsonObject = Gson().fromJson<JsonObject>(wikipediaJson.await(), JsonObject::class.java)
    val pages = jsonObject["query"].asJsonObject["pages"].asJsonObject
    val extract = pages[pages.keySet().first()].asJsonObject["extract"]
    return extract.asString
}

fun main() = runBlocking {
    val terms = listOf("Kotlin", "Athens", "Meetup")
    val extracts = mutableListOf<String>()
    terms.forEach {
        val json = async { fetchJson(it) }
        extracts += parseExtract(json)
    }
    extracts.forEach { println(it) }
}


