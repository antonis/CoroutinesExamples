import kotlinx.coroutines.*

fun main() = runBlocking {
    //Launch 1M coroutines
    repeat(1_000_000) { counter ->
        launch {
            print(", $counter")
        }
    }
    //try that with threads!
}
