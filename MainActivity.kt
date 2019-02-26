package com.euapps.gdgathens

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        googleButton.setOnClickListener { searchGoogle() }

        wikipediaButton.setOnClickListener {
            launch {
                searchResult.text = getString(R.string.searching_wikipedia)
                searchResult.text = searchWikipedia()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancelChildren()
    }

    private fun searchGoogle() = launch {
        val result: Deferred<String> = async(Dispatchers.IO) { google(searchInput.text.toString()) }
        searchResult.text = getString(R.string.searching_google)
        searchResult.text = result.await()
    }

    private suspend fun searchWikipedia() = withContext(Dispatchers.IO) {
        wikipedia(searchInput.text.toString())
    }
}

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
    return extract?.asString ?: "No results"
}