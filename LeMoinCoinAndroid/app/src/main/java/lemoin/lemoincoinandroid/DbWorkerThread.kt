package lemoin.lemoincoinandroid

import android.os.Handler
import android.os.HandlerThread

class DbWorkerThread(threadName: String) : HandlerThread(threadName) {

    private lateinit var mWorkerHandler: Handler

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        //mWorkerHandler = Handler(looper)

    }

    fun postTask(task: Runnable) {
        mWorkerHandler = Handler(looper)
        mWorkerHandler.post(task)
    }

}