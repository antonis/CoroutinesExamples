import kotlinx.coroutines.*

fun main() = runBlocking {
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }
    val supervisor = SupervisorJob()
    with(CoroutineScope(supervisor)) {
        val child1 = launch(handler) {
            println("Child1 is failing")
            throw AssertionError("child1 cancelled")
        }
        val child2 = launch {
            child1.join()
            println("Child1 cancelled: ${child1.isCancelled}")
            println("Child2 isActive: $isActive")
            try {
                delay(Long.MAX_VALUE)
            } finally {
                println("Finally Child2 isActive: $isActive")
            }
        }
        child1.join()
        println("Cancelling supervisor")
        supervisor.cancel()
        child2.join()
    }
}
