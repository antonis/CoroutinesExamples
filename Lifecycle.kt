//Example: Lifecycle

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class LifecycleAwareClass : CoroutineScope { //eg Activity

    //...

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    //...

    fun doSomethingImportant() {
        launch {
            //important process
        }
    }

    //...

    fun onDestroy() { //or similar finalization method
        //...
        job.cancel()
    }
}
