import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.sync.*
import org.jsoup.Jsoup

private fun google(keyword: String): String {
    val doc = Jsoup.connect("https://google.com/search?q=$keyword")
        .userAgent("Mozilla/5.0").get()
    val title = doc.selectFirst("h3.r a")?.text()
    val description = doc.selectFirst("span.st")?.text()
    return "$title. $description"
}

val mutex = Mutex()
val cache = mutableMapOf<String, String>()

private fun CoroutineScope.cache(keywords: ReceiveChannel<String>):  ReceiveChannel<String> = produce {
    for(keyword in keywords) {
        send(cache.getOrElse(keyword) {
            println("Googling")
            val result = google(keyword)
            mutex.withLock { cache[keyword] = result }
            return@getOrElse result
        })
    }
}

private fun CoroutineScope.getCountries(): ReceiveChannel<String> = produce {
    val someDuplicates = listOf("Australia", "Australia", "Australia", "Argentina")
    for (country in listOf(someDuplicates, countries).flatten()) send(country)
}

fun main() = runBlocking {
    val countries = getCountries()
    val google = cache(countries)
    for (i in 1..5){ //get five results
        println("Result $i: ${google.receive()}")
    }
    println("One more... ${google.receive()}")
}
