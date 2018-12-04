import kotlinx.coroutines.*

fun main() = runBlocking {
    //Cancel with timeout
    try {
        withTimeout(1000L) {
            repeat(1000) { counter ->
                print(" $counter")
                delay(100L)
            }
        }
    } catch (e: TimeoutCancellationException) {
        print(" Timeout\n")
    }

    //Cancel manually
    val job = launch {
        try {
            repeat(1000) { counter ->
                print(" $counter")
                delay(50L)
            }
        } catch (e: CancellationException) {
            print(" CancellationException\n")
        }
    }
    delay(1000L)
    job.cancel()
    job.join()
}
