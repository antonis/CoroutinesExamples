import kotlinx.coroutines.*
import org.jsoup.Jsoup

private fun google(keyword: String): String {
    val doc = Jsoup.connect("https://google.com/search?q=$keyword")
        .userAgent("Mozilla/5.0").get()
    val title = doc.selectFirst("h3.r a")?.text()
    val description = doc.selectFirst("span.st")?.text()
    return "$title. $description"
}

suspend fun google(keywords: List<String>) = coroutineScope {
    for (keyword in keywords) {
        println("Googling $keyword")
        launch {
            val result = google(keyword)
            println("Result for $keyword: $result")
        }
    }
}

suspend fun main() {
    google(countries)
}
