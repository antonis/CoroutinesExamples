import com.google.gson.*

private fun fetchJson(term: String): String {
    val url = "https://en.wikipedia.org/w/api.php" +
            "?action=query&format=json&prop=extracts" +
            "&exsectionformat=plain&exsentences=2&explaintext=1" +
            "&titles=$term"
    return java.net.URL(url).readText()
}

private fun parseExtract(wikipediaJson: String): String {
    val jsonObject = Gson().fromJson<JsonObject>(wikipediaJson, JsonObject::class.java)
    val pages = jsonObject["query"].asJsonObject["pages"].asJsonObject
    val extract = pages[pages.keySet().first()].asJsonObject["extract"]
    return extract.asString
}

fun main()  {
    val terms = listOf("Kotlin", "Athens", "Meetup")
    val extracts = mutableListOf<String>()
    terms.forEach {
        val json = fetchJson(it)
        extracts += parseExtract(json)
    }
    extracts.forEach { println(it) }
}

