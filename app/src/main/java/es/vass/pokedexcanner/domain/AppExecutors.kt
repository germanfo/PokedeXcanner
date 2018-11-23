package es.vass.pokedexcanner.domain

import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object AppExecutors {

    val diskIO: Executor = Executors.newSingleThreadExecutor()
    val networkIO: Executor = Executors.newScheduledThreadPool(3)
    val mainThread: Executor = MainThreadExecutor()


    private class MainThreadExecutor: Executor{
        val mainThreadHandler = android.os.Handler(Looper.getMainLooper())

        override fun execute(command: Runnable?) {
            mainThreadHandler.post(command)
        }

    }

}