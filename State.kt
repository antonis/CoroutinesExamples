import org.jsoup.Jsoup
import java.lang.Thread.sleep

private fun google(keyword: String): String {
    val doc = Jsoup.connect("https://google.com/search?q=$keyword")
        .userAgent("Mozilla/5.0").get()
    val title = doc.selectFirst("h3.r a")?.text()
    val description = doc.selectFirst("span.st")?.text()
    return "$title. $description"
}

object Cache {

    private val cache = mutableMapOf<String, String>()
    private val requested = mutableSetOf<String>()

    fun googleWithCache(keyword: String): String {
        return cache[keyword] ?: if (requested.add(keyword)) {
            println("Googling")
            val result = google(keyword)
            cache.put(keyword, result)
            requested.remove(keyword)
            return result
        } else {
            sleep(2000) //wait and retry?
            return googleWithCache(keyword)
        }
    }

}

fun main() {
    val someDuplicates = listOf("Australia", "Australia", "Australia", "Argentina")
    for (country in listOf(someDuplicates, countries).flatten()) {
        val result = Cache.googleWithCache(country)
        println("Result for $country: $result")
    }
}
