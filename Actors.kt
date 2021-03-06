import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import org.jsoup.Jsoup

private fun google(keyword: String): String {
    val doc = Jsoup.connect("https://google.com/search?q=$keyword")
        .userAgent("Mozilla/5.0").get()
    val title = doc.selectFirst("h3.r a")?.text()
    val description = doc.selectFirst("span.st")?.text()
    return "$title. $description"
}

sealed class CacheAction(val keyword: String)
class RetrieveAction(keyword: String, val value: CompletableDeferred<String?>) : CacheAction(keyword)
class StoreAction(keyword: String, val value: String) : CacheAction(keyword)

fun CoroutineScope.cacheActor() = actor<CacheAction> {
    val cache = mutableMapOf<String, String>() //state
    for (msg in channel) {
        when (msg) {
            is RetrieveAction -> msg.value.complete(cache[msg.keyword])
            is StoreAction -> cache[msg.keyword] = msg.value
        }
    }
}

private fun CoroutineScope.cache(keywords: ReceiveChannel<String>):  ReceiveChannel<String> = produce {
    val cache = cacheActor()
    for(keyword in keywords) {
        val value = CompletableDeferred<String?>()
        cache.send(RetrieveAction(keyword, value))
        val retrievedValue = value.await()
        if(retrievedValue != null) {
            println("Cached")
            send(retrievedValue!!)
        } else {
            println("Googling")
            val result = google(keyword)
            cache.send(StoreAction(keyword, result))
            send(result)
        }
    }
    cache.close()
}

private fun CoroutineScope.getCountries(): ReceiveChannel<String> = produce {
    val someDuplicates = listOf("Australia", "Australia", "Australia", "Argentina")
    for (country in listOf(someDuplicates, countries).flatten()) send(country)
}

fun main() = runBlocking {
    val countries = getCountries()
    val google = cache(countries)
    for (i in 1..10){ //get five results
        println("Result $i: ${google.receive()}")
    }
    println("One more... ${google.receive()}")
}
