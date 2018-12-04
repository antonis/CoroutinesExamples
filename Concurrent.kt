import org.jsoup.Jsoup
import com.google.gson.*
import kotlinx.coroutines.*

private fun google(keyword: String): String {
    val doc = Jsoup.connect("https://google.com/search?q=$keyword")
        .userAgent("Mozilla/5.0").get()
    val title = doc.selectFirst("h3.r a")?.text()
    val description = doc.selectFirst("span.st")?.text()
    return "$title. $description"
}

private fun wikipedia(keyword: String): String {
    val url = "https://en.wikipedia.org/w/api.php" +
            "?action=query&format=json&prop=extracts" +
            "&exsectionformat=plain&exsentences=2&explaintext=1" +
            "&titles=$keyword"
    val json = java.net.URL(url).readText()
    val jsonObject = Gson().fromJson<JsonObject>(json, JsonObject::class.java)
    val pages = jsonObject["query"].asJsonObject["pages"].asJsonObject
    val extract = pages[pages.keySet().first()].asJsonObject["extract"]
    return extract.asString
}

fun main() = runBlocking {
    val keyword = "Meetup"
    launch (Dispatchers.Default) {
        val gResult = async { google(keyword) }
        val wResult = async { wikipedia(keyword) }
        println("Google replied: ${gResult.await()} \n" +
                "Wikipedia replied: ${wResult.await()}"
        )
    }
    println("Launched Coroutine")
}

