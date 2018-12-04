import kotlinx.coroutines.*

fun main() = runBlocking {
    launch {
        delay(200L)
        println("Kotlin")
    }

    coroutineScope {
        launch {
            delay(300L)
            println("Athens")
        }

        delay(100L)
        println("Hello")
        //coroutineScope does NOT block the current thread while waiting for all children to complete
    }

    println("Meetup")
    //runBlocking DOES block the current thread while waiting for all children to complete
}
